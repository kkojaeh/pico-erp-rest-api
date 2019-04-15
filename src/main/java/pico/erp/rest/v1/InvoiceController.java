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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.invoice.InvoiceData;
import pico.erp.invoice.InvoiceId;
import pico.erp.invoice.InvoiceQuery;
import pico.erp.invoice.InvoiceRequests;
import pico.erp.invoice.InvoiceService;
import pico.erp.invoice.InvoiceStatusKind;
import pico.erp.invoice.InvoiceView;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.UserId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("invoice-controller-v1")
@RequestMapping(value = "/invoice", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class InvoiceController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  private MessageSource messageSource;

  @ComponentAutowired
  private InvoiceService invoiceService;

  @ComponentAutowired
  private InvoiceQuery invoiceQuery;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "송장 조회")
  @PreAuthorize("hasAnyRole('INVOICE_RECEIVER', 'INVOICE_MANAGER')")
  @GetMapping(value = "/invoices/{id}", consumes = MediaType.ALL_VALUE)
  public InvoiceData get(@PathVariable("id") InvoiceId id) {
    return invoiceService.get(id);
  }

  @ApiOperation(value = "송장 수령")
  @PutMapping("/invoices/{id}/receive")
  @PreAuthorize("hasAnyRole('INVOICE_RECEIVER', 'INVOICE_MANAGER')")
  public void receive(@PathVariable("id") InvoiceId id,
    @RequestBody InvoiceRequests.ReceiveRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setConfirmerId(UserId.from(userDetails.getUsername()));
    invoiceService.receive(request);
  }

  @ApiOperation(value = "송장 검색")
  @PreAuthorize("hasAnyRole('INVOICE_RECEIVER', 'INVOICE_MANAGER')")
  @GetMapping(value = "/invoices", consumes = MediaType.ALL_VALUE)
  public Page<InvoiceView> retrieve(InvoiceView.Filter filter,
    Pageable pageable, @AuthenticationPrincipal AuthorizedUser userDetails) {
    return invoiceQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "송장 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(InvoiceStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

}
