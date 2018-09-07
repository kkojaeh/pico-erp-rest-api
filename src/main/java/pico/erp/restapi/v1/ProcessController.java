package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.item.data.ItemId;
import pico.erp.process.ProcessQuery;
import pico.erp.process.ProcessRequests;
import pico.erp.process.ProcessRequests.CreateRequest;
import pico.erp.process.ProcessRequests.DeleteRequest;
import pico.erp.process.ProcessRequests.UpdateRequest;
import pico.erp.process.ProcessService;
import pico.erp.process.data.ProcessData;
import pico.erp.process.data.ProcessDifficultyKind;
import pico.erp.process.data.ProcessId;
import pico.erp.process.data.ProcessStatusKind;
import pico.erp.process.data.ProcessView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("process-controller-v1")
@RequestMapping(value = "/process", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProcessController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ProcessService processService;

  @Lazy
  @Autowired
  private ProcessQuery processQuery;

  @Autowired
  private MessageSource messageSource;

  @ApiOperation(value = "공정 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/processes")
  @PreAuthorize("hasRole('PROCESS_MANAGER')")
  public ProcessData create(@RequestBody CreateRequest request) {
    return processService.create(request);
  }


  @ApiOperation(value = "공정 삭제")
  @DeleteMapping("/processes/{id}")
  @PreAuthorize("hasRole('PROCESS_MANAGER')")
  public void delete(@PathVariable("id") ProcessId id) {
    processService.delete(new DeleteRequest(id));
  }

  @ApiOperation(value = "공정 조회")
  @PreAuthorize("hasAnyRole('PROCESS_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/processes/{id}", consumes = MediaType.ALL_VALUE)
  public ProcessData get(@PathVariable("id") ProcessId id) {
    return processService.get(id);
  }

  @ApiOperation(value = "공정 조회")
  @PreAuthorize("hasAnyRole('PROCESS_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/items/{itemId}", consumes = MediaType.ALL_VALUE)
  public ProcessData get(@PathVariable("itemId") ItemId itemId) {
    return processService.get(itemId);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "공정 난이도 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/process-difficulty-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> processDifficultyLabels() {
    Locale locale = LocaleContextHolder.getLocale();
    return Stream.of(ProcessDifficultyKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, locale)
        )
      );
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "공정 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/process-status-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> processStatusLabels() {
    Locale locale = LocaleContextHolder.getLocale();
    return Stream.of(ProcessStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, locale)
        )
      ).collect(Collectors.toList());
  }

  @ApiOperation(value = "공정 검색")
  @PreAuthorize("hasAnyRole('PROCESS_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/processes", consumes = MediaType.ALL_VALUE)
  public Page<ProcessView> retrieve(
    @ModelAttribute ProcessView.Filter filter,
    Pageable pageable) {
    return processQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "공정 수정")
  @PutMapping("/processes/{id}")
  @PreAuthorize("hasRole('PROCESS_MANAGER')")
  public void update(@PathVariable("id") ProcessId id,
    @RequestBody UpdateRequest request) {
    request.setId(id);
    processService.update(request);
  }

  @ApiOperation(value = "공정 계획 완료")
  @PutMapping("/processes/{id}/complete-plan")
  @PreAuthorize("hasRole('PROCESS_MANAGER')")
  public void update(@PathVariable("id") ProcessId id,
    @RequestBody ProcessRequests.CompletePlanRequest request) {
    request.setId(id);
    processService.completePlan(request);
  }

}
