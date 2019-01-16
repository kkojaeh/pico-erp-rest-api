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
import pico.erp.purchase.invoice.PurchaseInvoiceData;
import pico.erp.purchase.invoice.PurchaseInvoiceId;
import pico.erp.purchase.invoice.PurchaseInvoiceRequests;
import pico.erp.purchase.invoice.PurchaseInvoiceService;
import pico.erp.purchase.invoice.PurchaseInvoiceStatusKind;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("purchase-invoice-controller-v1")
@RequestMapping(value = "/purchase-invoice", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class PurchaseInvoiceController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private PurchaseInvoiceService purchaseInvoiceService;


  @ApiOperation(value = "발주 송장 취소")
  @PutMapping("/invoices/{id}/cancel")
  @PreAuthorize("hasAnyRole('PURCHASE_INVOICE_PUBLISHER', 'PURCHASE_INVOICE_MANAGER')")
  public void cancel(@PathVariable("id") PurchaseInvoiceId id,
    @RequestBody PurchaseInvoiceRequests.CancelRequest request) {
    request.setId(id);
    purchaseInvoiceService.cancel(request);
  }

  @ApiOperation(value = "발주 송장 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/invoices")
  @PreAuthorize("hasAnyRole('PURCHASE_INVOICE_PUBLISHER', 'PURCHASE_INVOICE_MANAGER')")
  public PurchaseInvoiceData create(@RequestBody PurchaseInvoiceRequests.CreateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    return purchaseInvoiceService.create(request);
  }

  @ApiOperation(value = "발주 송장 확정")
  @PutMapping("/invoices/{id}/determine")
  @PreAuthorize("hasAnyRole('PURCHASE_INVOICE_PUBLISHER', 'PURCHASE_INVOICE_MANAGER')")
  public void determine(@PathVariable("id") PurchaseInvoiceId id,
    @RequestBody PurchaseInvoiceRequests.DetermineRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    purchaseInvoiceService.determine(request);
  }

  @ApiOperation(value = "발주 송장 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/invoices/{id}/generate")
  @PreAuthorize("hasAnyRole('PURCHASE_INVOICE_PUBLISHER', 'PURCHASE_INVOICE_MANAGER')")
  public PurchaseInvoiceData generate(
    @PathVariable("id") PurchaseInvoiceId id,
    @RequestBody PurchaseInvoiceRequests.GenerateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    return purchaseInvoiceService.generate(request);
  }

  /*
  @ApiOperation(value = "발주 송장 삭제")
  @DeleteMapping("/invoices/{id}")
  @PreAuthorize("hasRole('PURCHASE_INVOICE_MANAGER')")
  public void delete(@PathVariable("id") PurchaseInvoiceId id) {
    purchaseInvoiceService.delete(new PurchaseInvoiceRequests.DeleteRequest(id));
  }
  */

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "발주 송장 조회")
  @PreAuthorize("hasAnyRole('PURCHASE_INVOICE_PUBLISHER', 'PURCHASE_INVOICE_MANAGER')")
  @GetMapping(value = "/invoices/{id}", consumes = MediaType.ALL_VALUE)
  public PurchaseInvoiceData get(@PathVariable("id") PurchaseInvoiceId id) {
    return purchaseInvoiceService.get(id);
  }


  @ApiOperation(value = "발주 송장 검색")
  @PreAuthorize("hasAnyRole('PURCHASE_INVOICE_PUBLISHER', 'PURCHASE_INVOICE_MANAGER')")
  @GetMapping(value = "/orders/{orderId}/invoices", consumes = MediaType.ALL_VALUE)
  public List<PurchaseInvoiceData> retrieve(@PathVariable("orderId") PurchaseOrderId orderId) {
    return purchaseInvoiceService.getAll(orderId);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "발주 송장 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(PurchaseInvoiceStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "발주 송장 수정")
  @PutMapping("/invoices/{id}")
  @PreAuthorize("hasAnyRole('PURCHASE_INVOICE_PUBLISHER', 'PURCHASE_INVOICE_MANAGER')")
  public void update(@PathVariable("id") PurchaseInvoiceId id,
    @RequestBody PurchaseInvoiceRequests.UpdateRequest request) {
    request.setId(id);
    purchaseInvoiceService.update(request);
  }

}
