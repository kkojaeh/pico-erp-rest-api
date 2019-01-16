package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.project.ProjectId;
import pico.erp.project.sale.item.ProjectSaleItemData;
import pico.erp.project.sale.item.ProjectSaleItemId;
import pico.erp.project.sale.item.ProjectSaleItemRequests;
import pico.erp.project.sale.item.ProjectSaleItemService;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("project-sale-item-controller-v1")
@RequestMapping(value = "/project", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProjectSaleItemController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ProjectSaleItemService projectSaleItemService;


  @ApiOperation(value = "프로젝트 판매 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/projects/{projectId}/sale-items")
  @PreAuthorize("hasRole('PROJECT_MANAGER')")
  public ProjectSaleItemData create(@PathVariable("projectId") ProjectId projectId,
    @RequestBody ProjectSaleItemRequests.CreateRequest request) {
    request.setProjectId(projectId);
    return projectSaleItemService.create(request);
  }


  @ApiOperation(value = "프로젝트 판매 품목 삭제")
  @DeleteMapping("/projects/{projectId}/sale-items/{id}")
  @PreAuthorize("hasRole('PROJECT_MANAGER')")
  public void delete(@PathVariable("projectId") ProjectId projectId,
    @PathVariable("id") ProjectSaleItemId id) {
    projectSaleItemService.delete(new ProjectSaleItemRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "프로젝트 판매 품목 조회")
  @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'PROJECT_ACCESSOR')")
  @GetMapping(value = "/projects/{projectId}/sale-items", consumes = MediaType.ALL_VALUE)
  public List<ProjectSaleItemData> getAll(@PathVariable("projectId") ProjectId projectId) {
    return projectSaleItemService.getAll(projectId);
  }

  @ApiOperation(value = "프로젝트 판매 품목 수정")
  @PutMapping("/projects/{projectId}/sale-items/{id}")
  @PreAuthorize("hasRole('PROJECT_MANAGER')")
  public void update(@PathVariable("projectId") ProjectId projectId,
    @PathVariable("id") ProjectSaleItemId id,
    @RequestBody ProjectSaleItemRequests.UpdateRequest request) {
    request.setId(id);
    projectSaleItemService.update(request);
  }

}
