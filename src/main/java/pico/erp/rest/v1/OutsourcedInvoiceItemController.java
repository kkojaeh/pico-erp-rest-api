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
import pico.erp.outsourced.invoice.OutsourcedInvoiceId;
import pico.erp.outsourced.invoice.item.OutsourcedInvoiceItemData;
import pico.erp.outsourced.invoice.item.OutsourcedInvoiceItemId;
import pico.erp.outsourced.invoice.item.OutsourcedInvoiceItemRequests;
import pico.erp.outsourced.invoice.item.OutsourcedInvoiceItemService;
import pico.erp.rest.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("outsourced-invoice-item-controller-v1")
@RequestMapping(value = "/outsourced-invoice", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class OutsourcedInvoiceItemController {

  @ComponentAutowired
  private OutsourcedInvoiceItemService outsourcedInvoiceItemService;

  @ApiOperation(value = "발주 송장 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/invoices/{invoiceId}/items")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public OutsourcedInvoiceItemData create(@PathVariable("invoiceId") OutsourcedInvoiceId invoiceId,
    @RequestBody OutsourcedInvoiceItemRequests.CreateRequest request) {
    request.setInvoiceId(invoiceId);
    return outsourcedInvoiceItemService.create(request);
  }

  @ApiOperation(value = "발주 송장 품목 삭제")
  @DeleteMapping("/invoices/{invoiceId}/items/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public void delete(@PathVariable("invoiceId") OutsourcedInvoiceId invoiceId,
    @PathVariable("id") OutsourcedInvoiceItemId id) {
    outsourcedInvoiceItemService.delete(new OutsourcedInvoiceItemRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "발주 송장 품목 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  @GetMapping(value = "/invoices/{invoiceId}/items", consumes = MediaType.ALL_VALUE)
  public List<OutsourcedInvoiceItemData> getAll(
    @PathVariable("invoiceId") OutsourcedInvoiceId invoiceId) {
    return outsourcedInvoiceItemService.getAll(invoiceId);
  }

  @ApiOperation(value = "발주 송장 품목 수정")
  @PutMapping("/invoices/{invoiceId}/items/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public void update(@PathVariable("invoiceId") OutsourcedInvoiceId invoiceId,
    @PathVariable("id") OutsourcedInvoiceItemId id,
    @RequestBody OutsourcedInvoiceItemRequests.UpdateRequest request) {
    request.setId(id);
    outsourcedInvoiceItemService.update(request);
  }

}
