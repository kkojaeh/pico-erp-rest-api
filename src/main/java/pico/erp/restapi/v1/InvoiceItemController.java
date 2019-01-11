package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.invoice.InvoiceId;
import pico.erp.invoice.item.InvoiceItemData;
import pico.erp.invoice.item.InvoiceItemId;
import pico.erp.invoice.item.InvoiceItemRequests;
import pico.erp.invoice.item.InvoiceItemService;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("invoice-item-controller-v1")
@RequestMapping(value = "/invoice", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class InvoiceItemController {

  @Lazy
  @Autowired
  private InvoiceItemService invoiceItemService;

  @ApiOperation(value = "송장 품목 조회")
  @PreAuthorize("hasAnyRole('INVOICE_RECEIVER', 'INVOICE_MANAGER')")
  @GetMapping(value = "/invoices/{invoiceId}/items", consumes = MediaType.ALL_VALUE)
  public List<InvoiceItemData> getAll(
    @PathVariable("invoiceId") InvoiceId invoiceId) {
    return invoiceItemService.getAll(invoiceId);
  }

  @ApiOperation(value = "송장 품목 수정")
  @PutMapping("/invoices/{invoiceId}/items/{id}")
  @PreAuthorize("hasAnyRole('INVOICE_RECEIVER', 'INVOICE_MANAGER')")
  public void update(@PathVariable("invoiceId") InvoiceId invoiceId,
    @PathVariable("id") InvoiceItemId id,
    @RequestBody InvoiceItemRequests.UpdateRequest request) {
    request.setId(id);
    invoiceItemService.update(request);
  }

}
