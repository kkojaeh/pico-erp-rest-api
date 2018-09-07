package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.audit.AuditService;
import pico.erp.audit.data.AuditId;
import pico.erp.audit.data.CommitData;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("audit-controller-v1")
@RequestMapping(value = "/audit", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class AuditController {

  @Lazy
  @Autowired
  private AuditService auditService;

  @ApiOperation(value = "회사 변경 조회")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  @GetMapping(value = "/company/{id}", consumes = MediaType.ALL_VALUE)
  public List<CommitData> company(@PathVariable("id") String id) {
    return auditService.get(AuditId.from("company", id));
  }

  @ApiOperation(value = "회사 주소지 변경 조회")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  @GetMapping(value = "/company-address/{id}", consumes = MediaType.ALL_VALUE)
  public List<CommitData> companyAddress(@PathVariable("id") String id) {
    return auditService.get(AuditId.from("company-address", id));
  }

  @ApiOperation(value = "회사 연락처 변경 조회")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  @GetMapping(value = "/company-contact/{id}", consumes = MediaType.ALL_VALUE)
  public List<CommitData> companyContact(@PathVariable("id") String id) {
    return auditService.get(AuditId.from("company-contact", id));
  }

  @ApiOperation(value = "부서 변경 조회")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/department/{id}", consumes = MediaType.ALL_VALUE)
  public List<CommitData> department(@PathVariable("id") String id) {
    return auditService.get(AuditId.from("department", id));
  }

  @ApiOperation(value = "그룹 변경 조회")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/group/{id}", consumes = MediaType.ALL_VALUE)
  public List<CommitData> group(@PathVariable("id") String id) {
    return auditService.get(AuditId.from("group", id));
  }

  @ApiOperation(value = "품목 변경 조회")
  @PreAuthorize("hasRole('ITEM_MANAGER')")
  @GetMapping(value = "/item/{id}", consumes = MediaType.ALL_VALUE)
  public List<CommitData> item(@PathVariable("id") String id) {
    return auditService.get(AuditId.from("item", id));
  }

  @ApiOperation(value = "품목 분류 변경 조회")
  @PreAuthorize("hasRole('ITEM_MANAGER')")
  @GetMapping(value = "/item-category/{id}", consumes = MediaType.ALL_VALUE)
  public List<CommitData> itemCategory(@PathVariable("id") String id) {
    return auditService.get(AuditId.from("item-category", id));
  }

  @ApiOperation(value = "공정 유형 변경 조회")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  @GetMapping(value = "/process-type/{id}", consumes = MediaType.ALL_VALUE)
  public List<CommitData> processType(@PathVariable("id") String id) {
    return auditService.get(AuditId.from("process-type", id));
  }

  @ApiOperation(value = "사전 공정 유형 변경 조회")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  @GetMapping(value = "/preprocess-type/{id}", consumes = MediaType.ALL_VALUE)
  public List<CommitData> preprocessType(@PathVariable("id") String id) {
    return auditService.get(AuditId.from("preprocess-type", id));
  }

  @ApiOperation(value = "프로젝트 변경 조회")
  @PreAuthorize("hasRole('PROJECT_MANAGER')")
  @GetMapping(value = "/project/{id}", consumes = MediaType.ALL_VALUE)
  public List<CommitData> project(@PathVariable("id") String id) {
    return auditService.get(AuditId.from("project", id));
  }

  @ApiOperation(value = "사용자 변경 조회")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/user/{id}", consumes = MediaType.ALL_VALUE)
  public List<CommitData> user(@PathVariable("id") String id) {
    return auditService.get(AuditId.from("user", id));
  }

}
