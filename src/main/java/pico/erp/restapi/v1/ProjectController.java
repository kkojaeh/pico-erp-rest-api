package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.project.ProjectData;
import pico.erp.project.ProjectId;
import pico.erp.project.ProjectQuery;
import pico.erp.project.ProjectRequests;
import pico.erp.project.ProjectRequests.DeleteRequest;
import pico.erp.project.ProjectRequests.UpdateRequest;
import pico.erp.project.ProjectService;
import pico.erp.project.ProjectView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("project-controller-v1")
@RequestMapping(value = "/project", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProjectController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ProjectService projectService;

  @Lazy
  @Autowired
  private ProjectQuery projectQuery;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "프로젝트 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/project-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asLabels(@RequestParam("query") String keyword) {
    return projectQuery.asLabels(keyword, labelQueryLimit);
  }

  @ApiOperation(value = "프로젝트 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/projects")
  @PreAuthorize("hasRole('PROJECT_MANAGER')")
  public void create(@RequestBody ProjectRequests.CreateRequest request) {
    projectService.create(request);
  }


  @ApiOperation(value = "프로젝트 삭제")
  @DeleteMapping("/projects/{id}")
  @PreAuthorize("hasRole('PROJECT_MANAGER')")
  public void delete(@PathVariable("id") ProjectId id) {
    projectService.delete(new DeleteRequest(id));
  }


  @CacheControl(maxAge = 300)
  @ApiOperation(value = "프로젝트 조회")
  @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'PROJECT_ACCESSOR')")
  @GetMapping(value = "/projects/{id}", consumes = MediaType.ALL_VALUE)
  public ProjectData get(@PathVariable("id") ProjectId id) {
    return projectService.get(id);
  }

  @ApiOperation(value = "프로젝트 검색")
  @PreAuthorize("hasAnyRole('PROJECT_MANAGER', 'PROJECT_ACCESSOR')")
  @GetMapping(value = "/projects", consumes = MediaType.ALL_VALUE)
  public Page<ProjectView> retrieve(@ModelAttribute ProjectView.Filter filter,
    Pageable pageable) {
    return projectQuery.retrieve(filter, pageable);
  }


  @ApiOperation(value = "프로젝트 수정")
  @PutMapping("/projects/{id}")
  @PreAuthorize("hasRole('PROJECT_MANAGER')")
  public void update(@PathVariable("id") ProjectId id,
    @RequestBody UpdateRequest request) {
    request.setId(id);
    projectService.update(request);
  }

}
