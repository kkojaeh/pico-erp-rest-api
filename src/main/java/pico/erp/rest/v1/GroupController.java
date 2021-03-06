package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Collection;
import java.util.List;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.UserId;
import pico.erp.user.group.GroupData;
import pico.erp.user.group.GroupId;
import pico.erp.user.group.GroupJoinedUserView;
import pico.erp.user.group.GroupQuery;
import pico.erp.user.group.GroupRequests;
import pico.erp.user.group.GroupRoleGrantedOrNotView;
import pico.erp.user.group.GroupService;
import pico.erp.user.group.GroupTransporter;
import pico.erp.user.group.GroupView;
import pico.erp.user.role.RoleId;


@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("user-group-controller-v1")
@RequestMapping(value = "/user", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class GroupController {

  @Value("${label.query.limit}")
  long labelQueryLimit;


  @ComponentAutowired
  private GroupService groupService;

  @ComponentAutowired
  private GroupQuery groupQuery;

  @ComponentAutowired
  private GroupTransporter groupTransporter;

  @ApiOperation(value = "그룹 사용자 추가")
  @PostMapping("/groups/{id}/users/{userId}")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void addUser(@PathVariable("id") GroupId id,
    @PathVariable("userId") UserId userId,
    @RequestBody GroupRequests.AddUserRequest request) {
    request.setId(id);
    request.setUserId(userId);
    groupService.addUser(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "그룹 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/group-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asLabels(
    @RequestParam("query") String keyword) {
    return groupQuery.asLabels(keyword, labelQueryLimit);
  }

  @ApiOperation(value = "그룹 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/groups")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public GroupData create(@RequestBody GroupRequests.CreateRequest request) {
    return groupService.create(request);
  }

  @ApiOperation(value = "그룹 삭제")
  @DeleteMapping("/groups/{id}")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void delete(@PathVariable("id") GroupId id) {
    groupService.delete(
      new GroupRequests.DeleteRequest(id)
    );
  }


  @SneakyThrows
  @ApiOperation(value = "export as xlsx")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/xlsx/groups", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> exportAs(GroupTransporter.ExportRequest request) {
    return SharedController.asResponse(groupTransporter.exportExcel(request));
  }

  @ApiOperation(value = "그룹 포함 사용자 조회")
  @GetMapping(value = "/groups/{id}/users", consumes = MediaType.ALL_VALUE)
  @PreAuthorize("hasRole('USER_MANAGER')")
  public Collection<GroupJoinedUserView> findAllGroupJoinedUser(
    @PathVariable("id") GroupId id) {
    return groupQuery.findAllGroupJoinedUser(id);
  }

  @ApiOperation(value = "그룹 권한 부여 상태 조회")
  @GetMapping(value = "/groups/{id}/roles", consumes = MediaType.ALL_VALUE)
  @PreAuthorize("hasAnyRole('USER_MANAGER', 'USER_ACCESSOR')")
  public Collection<GroupRoleGrantedOrNotView> findAllGroupRoleGranted(
    @PathVariable(name = "id", required = false) GroupId id) {
    return groupQuery.findAllGroupRoleGrantedOrNot(id);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "그룹 조회")
  @PreAuthorize("hasAnyRole('USER_MANAGER', 'USER_ACCESSOR')")
  @GetMapping(value = "/groups/{id}", consumes = MediaType.ALL_VALUE)
  public GroupData get(@PathVariable("id") GroupId id) {
    return groupService.get(id);
  }

  @ApiOperation(value = "그룹 권한 부여")
  @PostMapping("/groups/{id}/roles/{roleId}")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void grantRole(@PathVariable("id") GroupId id,
    @PathVariable("roleId") RoleId roleId,
    @RequestBody GroupRequests.GrantRoleRequest request) {
    request.setId(id);
    request.setRoleId(roleId);
    groupService.grantRole(request);
  }

  @SneakyThrows
  @ApiOperation(value = "import by xlsx")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @PostMapping(value = "/xlsx/groups", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public boolean importBy(@RequestPart MultipartFile file, GroupTransporter.ImportRequest request) {
    request.setInputStream(file.getInputStream());
    groupTransporter.importExcel(request);
    return true;
  }

  @ApiOperation(value = "그룹 사용자 제거")
  @DeleteMapping("/groups/{id}/users/{userId}")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void removeUser(@PathVariable("id") GroupId id,
    @PathVariable("userId") UserId userId,
    @RequestBody GroupRequests.RemoveUserRequest request) {
    request.setId(id);
    request.setUserId(userId);
    groupService.removeUser(request);
  }

  @ApiOperation(value = "그룹 검색")
  @PreAuthorize("hasAnyRole('USER_MANAGER', 'USER_ACCESSOR')")
  @GetMapping(value = "/groups", consumes = MediaType.ALL_VALUE)
  public Page<GroupView> retrieve(
    @ModelAttribute GroupView.Filter filter,
    Pageable pageable) {
    return groupQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "그룹 권한 폐지")
  @DeleteMapping("/groups/{id}/roles/{roleId}")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void revokeRole(@PathVariable("id") GroupId id,
    @PathVariable("roleId") RoleId roleId,
    @RequestBody GroupRequests.RevokeRoleRequest request) {
    request.setId(id);
    request.setRoleId(roleId);
    groupService.revokeRole(request);
  }

  @ApiOperation(value = "그룹 수정")
  @PutMapping("/groups/{id}")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void update(@PathVariable("id") GroupId id,
    @RequestBody GroupRequests.UpdateRequest request) {
    request.setId(id);
    groupService.update(request);
  }

}
