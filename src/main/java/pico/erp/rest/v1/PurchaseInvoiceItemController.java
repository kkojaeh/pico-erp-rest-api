package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
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
import pico.erp.purchase.invoice.PurchaseInvoiceId;
import pico.erp.purchase.invoice.item.PurchaseInvoiceItemData;
import pico.erp.purchase.invoice.item.PurchaseInvoiceItemId;
import pico.erp.purchase.invoice.item.PurchaseInvoiceItemRequests;
import pico.erp.purchase.invoice.item.PurchaseInvoiceItemService;
import pico.erp.rest.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("purchase-invoice-item-controller-v1")
@RequestMapping(value = "/purchase-invoice", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class PurchaseInvoiceItemController {

  @ComponentAutowired
  private PurchaseInvoiceItemService purchaseInvoiceItemService;

  @ApiOperation(value = "발주 송장 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/invoices/{invoiceId}/items")
  @PreAuthorize("hasAnyRole('PURCHASE_INVOICE_PUBLISHER', 'PURCHASE_INVOICE_MANAGER')")
  public PurchaseInvoiceItemData create(@PathVariable("invoiceId") PurchaseInvoiceId invoiceId,
    @RequestBody PurchaseInvoiceItemRequests.CreateRequest request) {
    request.setInvoiceId(invoiceId);
    return purchaseInvoiceItemService.create(request);
  }

  @ApiOperation(value = "발주 송장 품목 삭제")
  @DeleteMapping("/invoices/{invoiceId}/items/{id}")
  @PreAuthorize("hasAnyRole('PURCHASE_INVOICE_PUBLISHER', 'PURCHASE_INVOICE_MANAGER')")
  public void delete(@PathVariable("invoiceId") PurchaseInvoiceId invoiceId,
    @PathVariable("id") PurchaseInvoiceItemId id) {
    purchaseInvoiceItemService.delete(new PurchaseInvoiceItemRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "발주 송장 품목 조회")
  @PreAuthorize("hasAnyRole('PURCHASE_INVOICE_PUBLISHER', 'PURCHASE_INVOICE_MANAGER')")
  @GetMapping(value = "/invoices/{invoiceId}/items", consumes = MediaType.ALL_VALUE)
  public List<PurchaseInvoiceItemData> getAll(
    @PathVariable("invoiceId") PurchaseInvoiceId invoiceId) {
    return purchaseInvoiceItemService.getAll(invoiceId);
  }

  @ApiOperation(value = "발주 송장 품목 수정")
  @PutMapping("/invoices/{invoiceId}/items/{id}")
  @PreAuthorize("hasAnyRole('PURCHASE_INVOICE_PUBLISHER', 'PURCHASE_INVOICE_MANAGER')")
  public void update(@PathVariable("invoiceId") PurchaseInvoiceId invoiceId,
    @PathVariable("id") PurchaseInvoiceItemId id,
    @RequestBody PurchaseInvoiceItemRequests.UpdateRequest request) {
    request.setId(id);
    purchaseInvoiceItemService.update(request);
  }

}
