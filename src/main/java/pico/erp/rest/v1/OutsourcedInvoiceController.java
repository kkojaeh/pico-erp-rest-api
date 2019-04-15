package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.stream.Stream;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.outsourced.invoice.OutsourcedInvoiceData;
import pico.erp.outsourced.invoice.OutsourcedInvoiceId;
import pico.erp.outsourced.invoice.OutsourcedInvoiceQuery;
import pico.erp.outsourced.invoice.OutsourcedInvoiceRequests;
import pico.erp.outsourced.invoice.OutsourcedInvoiceService;
import pico.erp.outsourced.invoice.OutsourcedInvoiceStatusKind;
import pico.erp.outsourced.invoice.OutsourcedInvoiceView;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("outsourced-invoice-controller-v1")
@RequestMapping(value = "/outsourced-invoice", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class OutsourcedInvoiceController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @ComponentAutowired
  private OutsourcedInvoiceService outsourcedInvoiceService;

  @ComponentAutowired
  private OutsourcedInvoiceQuery outsourcedInvoiceQuery;


  @ApiOperation(value = "발주 송장 취소")
  @PutMapping("/invoices/{id}/cancel")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public void cancel(@PathVariable("id") OutsourcedInvoiceId id,
    @RequestBody OutsourcedInvoiceRequests.CancelRequest request) {
    request.setId(id);
    outsourcedInvoiceService.cancel(request);
  }

  @ApiOperation(value = "발주 송장 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/invoices")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public OutsourcedInvoiceData create(@RequestBody OutsourcedInvoiceRequests.CreateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    return outsourcedInvoiceService.create(request);
  }

  @ApiOperation(value = "발주 송장 확정")
  @PutMapping("/invoices/{id}/determine")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public void determine(@PathVariable("id") OutsourcedInvoiceId id,
    @RequestBody OutsourcedInvoiceRequests.DetermineRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    outsourcedInvoiceService.determine(request);
  }

  /*
  @ApiOperation(value = "발주 송장 삭제")
  @DeleteMapping("/invoices/{id}")
  @PreAuthorize("hasRole('OUTSOURCING_INVOICE_MANAGER')")
  public void delete(@PathVariable("id") OutsourcedInvoiceId id) {
    outsourcedInvoiceService.delete(new OutsourcedInvoiceRequests.DeleteRequest(id));
  }
  */

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "발주 송장 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  @GetMapping(value = "/invoices/{id}", consumes = MediaType.ALL_VALUE)
  public OutsourcedInvoiceData get(@PathVariable("id") OutsourcedInvoiceId id) {
    return outsourcedInvoiceService.get(id);
  }


  @ApiOperation(value = "발주 송장 검색")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER', 'OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  @GetMapping(value = "/invoices", consumes = MediaType.ALL_VALUE)
  public Page<OutsourcedInvoiceView> retrieve(@ModelAttribute OutsourcedInvoiceView.Filter filter,
    Pageable pageable) {
    return outsourcedInvoiceQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "발주 송장 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(OutsourcedInvoiceStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "발주 송장 수정")
  @PutMapping("/invoices/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public void update(@PathVariable("id") OutsourcedInvoiceId id,
    @RequestBody OutsourcedInvoiceRequests.UpdateRequest request) {
    request.setId(id);
    outsourcedInvoiceService.update(request);
  }

}
