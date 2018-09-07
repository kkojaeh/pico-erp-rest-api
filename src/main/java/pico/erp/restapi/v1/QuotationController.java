package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.quotation.QuotationQuery;
import pico.erp.quotation.QuotationRequests.CancelRequest;
import pico.erp.quotation.QuotationRequests.CommitRequest;
import pico.erp.quotation.QuotationRequests.DeleteRequest;
import pico.erp.quotation.QuotationRequests.DraftRequest;
import pico.erp.quotation.QuotationRequests.NextDraftRequest;
import pico.erp.quotation.QuotationRequests.PrepareRequest;
import pico.erp.quotation.QuotationRequests.PrintSheetRequest;
import pico.erp.quotation.QuotationRequests.UpdateRequest;
import pico.erp.quotation.QuotationService;
import pico.erp.quotation.data.QuotationData;
import pico.erp.quotation.data.QuotationExpiryPolicyKind;
import pico.erp.quotation.data.QuotationId;
import pico.erp.quotation.data.QuotationPrintSheetOptions;
import pico.erp.quotation.data.QuotationStatusCountPerMonthAggregateView;
import pico.erp.quotation.data.QuotationStatusCountPerMonthAggregateView.QuotationStatusCountPerMonthAggregateOptions;
import pico.erp.quotation.data.QuotationStatusKind;
import pico.erp.quotation.data.QuotationView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("quotation-controller-v1")
@RequestMapping(value = "/quotation", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class QuotationController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private QuotationService quotationService;

  @Lazy
  @Autowired
  private QuotationQuery quotationQuery;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "견적 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/quotation-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asLabels(@RequestParam("query") String keyword) {
    return quotationQuery.asLabels(keyword, labelQueryLimit);
  }

  @ApiOperation(value = "월별 상태 수 집계")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  @GetMapping(value = "/aggregate/statuses/count/month", consumes = MediaType.ALL_VALUE)
  public Collection<QuotationStatusCountPerMonthAggregateView> aggregate(
    QuotationStatusCountPerMonthAggregateOptions options) {
    return quotationQuery.aggregateCountStatusPerMonth(options);
  }

  @ApiOperation(value = "견적 취소")
  @PutMapping("/quotations/{id}/cancel")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void cancel(@PathVariable("id") QuotationId id,
    @RequestBody CancelRequest request) {
    request.setId(id);
    quotationService.cancel(request);
  }

  @ApiOperation(value = "견적 제출")
  @PutMapping("/quotations/{id}/commit")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void commit(@PathVariable("id") QuotationId id,
    @RequestBody CommitRequest request) {
    request.setId(id);
    quotationService.commit(request);
  }

  @ApiOperation(value = "견적 준비 완료")
  @PutMapping("/quotations/{id}/prepare")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void commit(@PathVariable("id") QuotationId id,
    @RequestBody PrepareRequest request) {
    request.setId(id);
    quotationService.prepare(request);
  }

  @ApiOperation(value = "견적 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/quotations")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void create(@RequestBody DraftRequest request) {
    quotationService.draft(request);
  }

  @ApiOperation(value = "견적 삭제")
  @DeleteMapping("/quotations/{id}")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void delete(@PathVariable("id") QuotationId id) {
    quotationService.delete(new DeleteRequest(id));
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "만료 정책 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/expiry-policy-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> expiryPolicies() {
    return Stream.of(QuotationExpiryPolicyKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "견적 조회")
  @PreAuthorize("hasAnyRole('QUOTATION_MANAGER', 'QUOTATION_ACCESSOR')")
  @GetMapping(value = "/quotations/{id}", consumes = MediaType.ALL_VALUE)
  public QuotationData get(@PathVariable("id") QuotationId id) {
    return quotationService.get(id);
  }

  @ApiOperation(value = "재견적")
  @PostMapping("/quotations/{id}/next")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public QuotationData nextDraft(@PathVariable("id") QuotationId id,
    @RequestBody NextDraftRequest request) {
    request.setId(id);
    return quotationService.nextDraft(request);
  }

  @SneakyThrows
  @ApiOperation(value = "견적서 다운로드")
  @PreAuthorize("hasAnyRole('QUOTATION_MANAGER', 'QUOTATION_ACCESSOR')")
  @GetMapping(value = "/quotations/{id}/print-sheet", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> printSheet(@PathVariable("id") QuotationId id,
    QuotationPrintSheetOptions options) {
    return SharedController.asResponse(quotationService.printSheet(
      new PrintSheetRequest(id, options)
    ));
  }

  @ApiOperation(value = "견적 검색")
  @PreAuthorize("hasAnyRole('QUOTATION_MANAGER', 'QUOTATION_ACCESSOR')")
  @GetMapping(value = "/quotations", consumes = MediaType.ALL_VALUE)
  public Page<QuotationView> retrieve(QuotationView.Filter filter,
    Pageable pageable) {
    return quotationQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "견적 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(QuotationStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "견적 수정")
  @PutMapping("/quotations/{id}")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void update(@PathVariable("id") QuotationId id,
    @RequestBody UpdateRequest request) {
    request.setId(id);
    quotationService.update(request);
  }


}
