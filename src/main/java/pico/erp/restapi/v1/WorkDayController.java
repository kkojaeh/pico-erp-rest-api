package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
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
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.workday.WorkDayQuery;
import pico.erp.workday.WorkDayRequests;
import pico.erp.workday.WorkDayService;
import pico.erp.workday.data.WorkDayCategory;
import pico.erp.workday.data.WorkDayCategoryId;
import pico.erp.workday.data.WorkDayData;
import pico.erp.workday.data.WorkDayId;
import pico.erp.workday.data.WorkDayView;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("work-day-controller-v1")
@RequestMapping(value = "/work-day", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WorkDayController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private WorkDayService workDayService;

  @Lazy
  @Autowired
  private WorkDayQuery workDayQuery;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "작업일 분류 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/category-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asCategoryLabels() {
    return workDayQuery.asCategoryLabels();
  }

  @ApiOperation(value = "작업일 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/work-days")
  @PreAuthorize("hasRole('WORK_DAY_MANAGER')")
  public void create(
    @RequestBody WorkDayRequests.CreateRequest request) {
    workDayService.create(request);
  }

  @ApiOperation(value = "작업일 삭제")
  @DeleteMapping("/work-days/{id}")
  @PreAuthorize("hasRole('WORK_DAY_MANAGER')")
  public void delete(
    @PathVariable("id") WorkDayId id) {
    workDayService.delete(new WorkDayRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "작업일 생성")
  @PostMapping("/generate-work-days")
  @PreAuthorize("hasRole('WORK_DAY_MANAGER')")
  public void generate(@RequestBody WorkDayRequests.GenerateRequest request) {
    workDayService.generate(request);
  }

  @ApiOperation(value = "작업일 검색")
  @PreAuthorize("hasAnyRole('WORK_DAY_MANAGER', 'WORK_DAY_ACCESSOR')")
  @GetMapping(value = "/work-days", consumes = MediaType.ALL_VALUE)
  public List<WorkDayView> retrieve(
    @ModelAttribute WorkDayView.Filter filter) {
    return workDayQuery.retrieve(filter);
  }

  @ApiOperation(value = "작업일 조회")
  @PreAuthorize("hasAnyRole('WORK_DAY_MANAGER', 'WORK_DAY_ACCESSOR')")
  @GetMapping(value = "/work-days/{id}", consumes = MediaType.ALL_VALUE)
  public WorkDayData get(
    @PathVariable("id") WorkDayId id) {
    return workDayService.get(id);
  }

  @ApiOperation(value = "작업일 조회")
  @PreAuthorize("hasAnyRole('WORK_DAY_MANAGER', 'WORK_DAY_ACCESSOR')")
  @GetMapping(value = "/categories/{id}", consumes = MediaType.ALL_VALUE)
  public WorkDayCategory get(
    @PathVariable("id") WorkDayCategoryId id) {
    return workDayService.get(id);
  }


  @ApiOperation(value = "작업일 수정")
  @PutMapping("/work-days/{id}")
  @PreAuthorize("hasRole('WORK_DAY_MANAGER')")
  public void update(
    @PathVariable("id") WorkDayId id,
    @RequestBody WorkDayRequests.UpdateRequest request) {
    request.setId(id);
    workDayService.update(request);
  }

}
