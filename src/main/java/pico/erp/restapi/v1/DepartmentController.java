package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import pico.erp.user.department.DepartmentData;
import pico.erp.user.department.DepartmentId;
import pico.erp.user.department.DepartmentQuery;
import pico.erp.user.department.DepartmentRequests;
import pico.erp.user.department.DepartmentService;
import pico.erp.user.department.DepartmentTransporter;
import pico.erp.user.department.DepartmentView;


@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("user-department-controller-v1")
@RequestMapping(value = "/user", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class DepartmentController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private DepartmentService departmentService;

  @Lazy
  @Autowired
  private DepartmentQuery departmentQuery;

  @Lazy
  @Autowired
  private DepartmentTransporter departmentTransporter;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "부서 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/department-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asLabels(
    @RequestParam("query") String keyword) {
    return departmentQuery.asLabels(keyword, labelQueryLimit);
  }

  @ApiOperation(value = "부서 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/departments")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void create(@RequestBody DepartmentRequests.CreateRequest request) {
    departmentService.create(request);
  }

  @ApiOperation(value = "부서 삭제")
  @DeleteMapping("/departments/{id}")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void delete(@PathVariable("id") DepartmentId id) {
    departmentService.delete(new DepartmentRequests.DeleteRequest(id));
  }

  @SneakyThrows
  @ApiOperation(value = "export as xlsx")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/xlsx/departments", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> exportAs(DepartmentTransporter.ExportRequest request) {
    return SharedController.asResponse(departmentTransporter.exportExcel(request));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "부서 조회")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/departments/{id}", consumes = MediaType.ALL_VALUE)
  public DepartmentData get(@PathVariable("id") DepartmentId id) {
    return departmentService.get(id);
  }


  @SneakyThrows
  @ApiOperation(value = "import by xlsx")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @PostMapping(value = "/xlsx/departments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public boolean importBy(@RequestPart MultipartFile file,
    DepartmentTransporter.ImportRequest request) {
    request.setInputStream(file.getInputStream());
    departmentTransporter.importExcel(request);
    return true;
  }


  @ApiOperation(value = "부서 검색")
  @PreAuthorize("hasRole('USER_MANAGER')")
  @GetMapping(value = "/departments", consumes = MediaType.ALL_VALUE)
  public Page<DepartmentView> retrieve(
    @ModelAttribute DepartmentView.Filter filter,
    Pageable pageable) {
    return departmentQuery.retrieve(filter, pageable);
  }


  @ApiOperation(value = "부서 수정")
  @PutMapping("/departments/{id}")
  @PreAuthorize("hasRole('USER_MANAGER')")
  public void update(@PathVariable("id") DepartmentId id,
    @RequestBody DepartmentRequests.UpdateRequest request) {
    request.setId(id);
    departmentService.update(request);
  }


}
