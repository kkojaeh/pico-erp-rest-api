package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.UserQuery;
import pico.erp.user.UserRequests;
import pico.erp.user.UserRequests.GrantRoleRequest;
import pico.erp.user.UserRequests.RevokeRoleRequest;
import pico.erp.user.UserRequests.UpdateRequest;
import pico.erp.user.UserService;
import pico.erp.user.UserXporter;
import pico.erp.user.data.UserData;
import pico.erp.user.data.UserId;
import pico.erp.user.data.UserRoleGrantedOrNotView;
import pico.erp.user.data.UserView;


@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("user-controller-v1")
@RequestMapping(value = "/user", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class UserController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private UserQuery userQuery;

  @Lazy
  @Autowired
  private UserXporter userXporter;


  @CacheControl(maxAge = 300)
  @ApiOperation(value = "사용자 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/user-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asLabels(@RequestParam("query") String keyword) {
    return userQuery.asLabels(keyword, labelQueryLimit);
  }

  @ApiOperation(value = "사용자 삭제")
  @DeleteMapping("/users/{id}")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void delete(@PathVariable("id") UserId id) {
    userService.delete(
      new UserRequests.DeleteRequest(id)
    );
  }


  @ApiOperation(value = "사용자 권한 부여 상태 조회")
  @GetMapping(value = "/users/{id}/roles", consumes = MediaType.ALL_VALUE)
  @PreAuthorize("hasRole('USER_MANAGER')")
  public Collection<UserRoleGrantedOrNotView> findAllUserRoleGranted(
    @PathVariable("id") UserId id) {
    return userQuery.findAllUserRoleGrantedOrNot(id);
  }

  @ApiOperation(value = "내 정보 조회")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/me", consumes = MediaType.ALL_VALUE)
  public UserData getMe(@AuthenticationPrincipal AuthorizedUser userDetails) {
    return userService.get(UserId.from(userDetails.getUsername()));
  }

  @SneakyThrows
  @ApiOperation(value = "사용자 export as xlsx")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/export/users/xlsx", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> exportAs(UserXporter.ExportRequest request) {
    return SharedController.asResponse(userXporter.exportExcel(request));
  }

  @ApiOperation(value = "사용자 조회")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/users/{id}", consumes = MediaType.ALL_VALUE)
  public UserData get(@PathVariable("id") UserId id,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    boolean isManager = userDetails.getAuthorities()
      .stream()
      .map(GrantedAuthority::getAuthority)
      .anyMatch(authority -> "ROLE_USER_MANAGER".equals(authority));

    UserData user = userService.get(id);

    if (!isManager) {
      user.setGroups(Collections.emptySet());
      user.setRoles(Collections.emptySet());
      user.setWholeRoles(Collections.emptySet());
      user.setMobilePhoneNumber(null);
    }
    return user;
  }

  @ApiOperation(value = "사용자 권한 부여")
  @PostMapping("/users/{id}/roles")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void grantRole(@PathVariable("id") UserId id,
    @RequestBody GrantRoleRequest request) {
    request.setId(id);
    userService.grantRole(request);
  }

  @SneakyThrows
  @ApiOperation(value = "사용자 import by xlsx")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @PostMapping(value = "/import/users/xlsx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public boolean importBy(@RequestPart MultipartFile file, UserXporter.ImportRequest request) {
    request.setInputStream(file.getInputStream());
    userXporter.importExcel(request);
    return true;
  }

  @ApiOperation(value = "사용자 검색")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/users", consumes = MediaType.ALL_VALUE)
  public Page<UserView> retrieve(@ModelAttribute UserView.Filter filter,
    @PageableDefault Pageable pageable) {
    return userQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "사용자 권한 폐지")
  @DeleteMapping("/users/{id}/roles")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void revokeRole(@PathVariable("id") UserId id,
    @RequestBody RevokeRoleRequest request) {
    request.setId(id);
    userService.revokeRole(request);
  }

  @ApiOperation(value = "사용자 수정")
  @PutMapping("/users/{id}")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void update(@PathVariable("id") UserId id, @RequestBody UpdateRequest request) {
    request.setId(id);
    userService.update(request);
  }

}
