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
import pico.erp.project.charge.ProjectChargeRequests;
import pico.erp.project.charge.ProjectChargeService;
import pico.erp.project.charge.data.ProjectChargeData;
import pico.erp.project.charge.data.ProjectChargeId;
import pico.erp.project.data.ProjectId;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("project-charge-controller-v1")
@RequestMapping(value = "/project", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProjectChargeController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ProjectChargeService projectChargeService;

  @ApiOperation(value = "프로젝트 비용 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/projects/{projectId}/charges")
  @PreAuthorize("hasRole('PROJECT_MANAGER')")
  public void create(@PathVariable("projectId") ProjectId projectId,
    @RequestBody ProjectChargeRequests.CreateRequest request) {
    request.setProjectId(projectId);
    projectChargeService.create(request);
  }

  @ApiOperation(value = "프로젝트 비용 삭제")
  @DeleteMapping("/projects/{projectId}/charges/{id}")
  @PreAuthorize("hasRole('PROJECT_MANAGER')")
  public void delete(@PathVariable("projectId") ProjectId projectId,
    @PathVariable("id") ProjectChargeId id) {
    projectChargeService.delete(new ProjectChargeRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "프로젝트 비용 조회")
  @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'PROJECT_ACCESSOR')")
  @GetMapping(value = "/projects/{projectId}/charges", consumes = MediaType.ALL_VALUE)
  public List<ProjectChargeData> getAll(@PathVariable("projectId") ProjectId projectId) {
    return projectChargeService.getAll(projectId);
  }

  @ApiOperation(value = "프로젝트 비용 수정")
  @PutMapping("/projects/{projectId}/charges/{id}")
  @PreAuthorize("hasRole('PROJECT_MANAGER')")
  public void update(@PathVariable("projectId") ProjectId projectId,
    @PathVariable("id") ProjectChargeId id,
    @RequestBody ProjectChargeRequests.UpdateRequest request) {
    request.setId(id);
    projectChargeService.update(request);
  }

}
