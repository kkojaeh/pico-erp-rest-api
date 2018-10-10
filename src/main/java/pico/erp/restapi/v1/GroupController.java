package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Collection;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
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
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.group.GroupData;
import pico.erp.user.group.GroupId;
import pico.erp.user.group.GroupJoinedUserView;
import pico.erp.user.group.GroupQuery;
import pico.erp.user.group.GroupRequests;
import pico.erp.user.group.GroupRoleGrantedOrNotView;
import pico.erp.user.group.GroupService;
import pico.erp.user.group.GroupView;
import pico.erp.user.group.GroupXporter;


@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("user-group-controller-v1")
@RequestMapping(value = "/user", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class GroupController {

  @Value("${label.query.limit}")
  long labelQueryLimit;


  @Lazy
  @Autowired
  private GroupService groupService;

  @Lazy
  @Autowired
  private GroupQuery groupQuery;

  @Lazy
  @Autowired
  private GroupXporter groupXporter;

  @ApiOperation(value = "그룹 사용자 추가")
  @PostMapping("/groups/{id}/users")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void addUser(@PathVariable("id") GroupId id,
    @RequestBody GroupRequests.AddUserRequest request) {
    request.setId(id);
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
  public void create(@RequestBody GroupRequests.CreateRequest request) {
    groupService.create(request);
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
  @ApiOperation(value = "그룹 export as xlsx")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/export/groups/xlsx", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> exportAs(GroupXporter.ExportRequest request) {
    return SharedController.asResponse(groupXporter.exportExcel(request));
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
  @PreAuthorize("hasRole('USER_MANAGER')")
  public Collection<GroupRoleGrantedOrNotView> findAllGroupRoleGranted(
    @PathVariable(name = "id", required = false) GroupId id) {
    return groupQuery.findAllGroupRoleGrantedOrNot(id);
  }

  @ApiOperation(value = "그룹 조회")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/groups/{id}", consumes = MediaType.ALL_VALUE)
  public GroupData get(@PathVariable("id") GroupId id) {
    return groupService.get(id);
  }

  @ApiOperation(value = "그룹 권한 부여")
  @PostMapping("/groups/{id}/roles")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void grantRole(@PathVariable(name = "id", required = false) GroupId id,
    @RequestBody GroupRequests.GrantRoleRequest request) {
    request.setId(id);
    groupService.grantRole(request);
  }

  @SneakyThrows
  @ApiOperation(value = "그룹 import by xlsx")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @PostMapping(value = "/import/groups/xlsx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public boolean importBy(@RequestPart MultipartFile file, GroupXporter.ImportRequest request) {
    request.setInputStream(file.getInputStream());
    groupXporter.importExcel(request);
    return true;
  }

  @ApiOperation(value = "그룹 사용자 제거")
  @DeleteMapping("/groups/{id}/users")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void removeUser(@PathVariable("id") GroupId id,
    @RequestBody GroupRequests.RemoveUserRequest request) {
    request.setId(id);
    groupService.removeUser(request);
  }

  @ApiOperation(value = "그룹 검색")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/groups", consumes = MediaType.ALL_VALUE)
  public Page<GroupView> retrieve(
    @ModelAttribute GroupView.Filter filter,
    Pageable pageable) {
    return groupQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "그룹 권한 폐지")
  @DeleteMapping("/groups/{id}/roles")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void revokeRole(@PathVariable("id") GroupId id,
    @RequestBody GroupRequests.RevokeRoleRequest request) {
    request.setId(id);
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
