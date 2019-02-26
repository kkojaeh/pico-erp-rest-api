package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceData;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceId;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceRequests;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceService;
import pico.erp.outsourcing.invoice.OutsourcingInvoiceStatusKind;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("outsourcing-invoice-controller-v1")
@RequestMapping(value = "/outsourcing-invoice", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class OutsourcingInvoiceController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private OutsourcingInvoiceService outsourcingInvoiceService;


  @ApiOperation(value = "발주 송장 취소")
  @PutMapping("/invoices/{id}/cancel")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public void cancel(@PathVariable("id") OutsourcingInvoiceId id,
    @RequestBody OutsourcingInvoiceRequests.CancelRequest request) {
    request.setId(id);
    outsourcingInvoiceService.cancel(request);
  }

  @ApiOperation(value = "발주 송장 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/invoices")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public OutsourcingInvoiceData create(
    @RequestBody OutsourcingInvoiceRequests.CreateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    return outsourcingInvoiceService.create(request);
  }

  @ApiOperation(value = "발주 송장 확정")
  @PutMapping("/invoices/{id}/determine")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public void determine(@PathVariable("id") OutsourcingInvoiceId id,
    @RequestBody OutsourcingInvoiceRequests.DetermineRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    outsourcingInvoiceService.determine(request);
  }

  @ApiOperation(value = "발주 송장 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/invoices/{id}/generate")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public OutsourcingInvoiceData generate(
    @PathVariable("id") OutsourcingInvoiceId id,
    @RequestBody OutsourcingInvoiceRequests.GenerateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    return outsourcingInvoiceService.generate(request);
  }

  /*
  @ApiOperation(value = "발주 송장 삭제")
  @DeleteMapping("/invoices/{id}")
  @PreAuthorize("hasRole('OUTSOURCING_INVOICE_MANAGER')")
  public void delete(@PathVariable("id") OutsourcingInvoiceId id) {
    outsourcingInvoiceService.delete(new OutsourcingInvoiceRequests.DeleteRequest(id));
  }
  */

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "발주 송장 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  @GetMapping(value = "/invoices/{id}", consumes = MediaType.ALL_VALUE)
  public OutsourcingInvoiceData get(@PathVariable("id") OutsourcingInvoiceId id) {
    return outsourcingInvoiceService.get(id);
  }


  @ApiOperation(value = "발주 송장 검색")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER', 'OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  @GetMapping(value = "/orders/{orderId}/invoices", consumes = MediaType.ALL_VALUE)
  public List<OutsourcingInvoiceData> retrieve(
    @PathVariable("orderId") OutsourcingOrderId orderId) {
    return outsourcingInvoiceService.getAll(orderId);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "발주 송장 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(OutsourcingInvoiceStatusKind.values())
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
  public void update(@PathVariable("id") OutsourcingInvoiceId id,
    @RequestBody OutsourcingInvoiceRequests.UpdateRequest request) {
    request.setId(id);
    outsourcingInvoiceService.update(request);
  }

}
