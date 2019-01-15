package pico.erp.restapi.config.notify.invoice;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.invoice.InvoiceId;
import pico.erp.invoice.InvoiceService;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.notify.type.NotifyTypeDefinition;
import pico.erp.notify.type.NotifyTypeId;
import pico.erp.restapi.config.notify.NotifyContextFactory;
import pico.erp.restapi.config.notify.purchase.request.PurchaseRequestNotifySubjectTypeDefinition;
import pico.erp.shared.Public;
import pico.erp.user.UserService;

@Public
@Component
public class InvoiceCreatedNotifyTypeDefinition implements NotifyTypeDefinition<InvoiceId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("invoice-created");

  @Lazy
  @Autowired
  private InvoiceService invoiceService;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Lazy
  @Autowired
  private UserService userService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Override
  public Object createContext(InvoiceId key) {
    val context = contextFactory.factory();
    val invoice = invoiceService.get(key);
    context.put("receiver", companyService.get(invoice.getReceiverId()));
    context.put("sender", companyService.get(invoice.getSenderId()));
    context.put("invoice", invoice);
    return context;
  }

  @Override
  public InvoiceId createKey(String key) {
    return InvoiceId.from(key);
  }

  @Override
  public NotifyTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "송장 수령 예정 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return PurchaseRequestNotifySubjectTypeDefinition.ID;
  }
}
