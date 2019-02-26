package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import pico.erp.outsourcing.invoice.OutsourcingInvoiceId;
import pico.erp.outsourcing.invoice.item.OutsourcingInvoiceItemData;
import pico.erp.outsourcing.invoice.item.OutsourcingInvoiceItemId;
import pico.erp.outsourcing.invoice.item.OutsourcingInvoiceItemRequests;
import pico.erp.outsourcing.invoice.item.OutsourcingInvoiceItemService;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("outsourcing-invoice-item-controller-v1")
@RequestMapping(value = "/outsourcing-invoice", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class OutsourcingInvoiceItemController {

  @Lazy
  @Autowired
  private OutsourcingInvoiceItemService outsourcingInvoiceItemService;

  @ApiOperation(value = "발주 송장 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/invoices/{invoiceId}/items")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public OutsourcingInvoiceItemData create(
    @PathVariable("invoiceId") OutsourcingInvoiceId invoiceId,
    @RequestBody OutsourcingInvoiceItemRequests.CreateRequest request) {
    request.setInvoiceId(invoiceId);
    return outsourcingInvoiceItemService.create(request);
  }

  @ApiOperation(value = "발주 송장 품목 삭제")
  @DeleteMapping("/invoices/{invoiceId}/items/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public void delete(@PathVariable("invoiceId") OutsourcingInvoiceId invoiceId,
    @PathVariable("id") OutsourcingInvoiceItemId id) {
    outsourcingInvoiceItemService.delete(new OutsourcingInvoiceItemRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "발주 송장 품목 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  @GetMapping(value = "/invoices/{invoiceId}/items", consumes = MediaType.ALL_VALUE)
  public List<OutsourcingInvoiceItemData> getAll(
    @PathVariable("invoiceId") OutsourcingInvoiceId invoiceId) {
    return outsourcingInvoiceItemService.getAll(invoiceId);
  }

  @ApiOperation(value = "발주 송장 품목 수정")
  @PutMapping("/invoices/{invoiceId}/items/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_INVOICE_PUBLISHER', 'OUTSOURCING_INVOICE_MANAGER')")
  public void update(@PathVariable("invoiceId") OutsourcingInvoiceId invoiceId,
    @PathVariable("id") OutsourcingInvoiceItemId id,
    @RequestBody OutsourcingInvoiceItemRequests.UpdateRequest request) {
    request.setId(id);
    outsourcingInvoiceItemService.update(request);
  }

}
