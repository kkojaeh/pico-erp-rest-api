package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.work.schedule.WorkScheduleData;
import pico.erp.work.schedule.WorkScheduleId;
import pico.erp.work.schedule.WorkScheduleQuery;
import pico.erp.work.schedule.WorkScheduleRequests;
import pico.erp.work.schedule.WorkScheduleService;
import pico.erp.work.schedule.WorkScheduleView;
import pico.erp.work.schedule.category.WorkScheduleCategory;
import pico.erp.work.schedule.category.WorkScheduleCategoryId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("work-schedule-controller-v1")
@RequestMapping(value = "/work-schedule", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WorkScheduleController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @ComponentAutowired
  private WorkScheduleService workScheduleService;

  @ComponentAutowired
  private WorkScheduleQuery workScheduleQuery;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "작업일 분류 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/category-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asCategoryLabels() {
    return workScheduleQuery.asCategoryLabels();
  }

  @ApiOperation(value = "작업일 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/work-schedules")
  @PreAuthorize("hasRole('WORK_SCHEDULE_MANAGER')")
  public WorkScheduleData create(
    @RequestBody WorkScheduleRequests.CreateRequest request) {
    return workScheduleService.create(request);
  }

  @ApiOperation(value = "작업일 삭제")
  @DeleteMapping("/work-schedules/{id}")
  @PreAuthorize("hasRole('WORK_SCHEDULE_MANAGER')")
  public void delete(
    @PathVariable("id") WorkScheduleId id) {
    workScheduleService.delete(new WorkScheduleRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "작업일 생성")
  @PostMapping("/generate-work-schedules")
  @PreAuthorize("hasRole('WORK_SCHEDULE_MANAGER')")
  public void generate(@RequestBody WorkScheduleRequests.GenerateRequest request) {
    workScheduleService.generate(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "작업일 조회")
  @PreAuthorize("hasAnyRole('WORK_SCHEDULE_MANAGER', 'WORK_SCHEDULE_ACCESSOR')")
  @GetMapping(value = "/work-schedules/{id}", consumes = MediaType.ALL_VALUE)
  public WorkScheduleData get(
    @PathVariable("id") WorkScheduleId id) {
    return workScheduleService.get(id);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "작업일 조회")
  @PreAuthorize("hasAnyRole('WORK_SCHEDULE_MANAGER', 'WORK_SCHEDULE_ACCESSOR')")
  @GetMapping(value = "/categories/{id}", consumes = MediaType.ALL_VALUE)
  public WorkScheduleCategory get(
    @PathVariable("id") WorkScheduleCategoryId id) {
    return workScheduleService.get(id);
  }

  @ApiOperation(value = "작업일 검색")
  @PreAuthorize("hasAnyRole('WORK_SCHEDULE_MANAGER', 'WORK_SCHEDULE_ACCESSOR')")
  @GetMapping(value = "/work-schedules", consumes = MediaType.ALL_VALUE)
  public List<WorkScheduleView> retrieve(
    @ModelAttribute WorkScheduleView.Filter filter) {
    return workScheduleQuery.retrieve(filter);
  }

  @ApiOperation(value = "작업일 수정")
  @PutMapping("/work-schedules/{id}")
  @PreAuthorize("hasRole('WORK_SCHEDULE_MANAGER')")
  public void update(
    @PathVariable("id") WorkScheduleId id,
    @RequestBody WorkScheduleRequests.UpdateRequest request) {
    request.setId(id);
    workScheduleService.update(request);
  }

}
