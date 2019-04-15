package pico.erp.rest.config.notify.invoice;

import kkojaeh.spring.boot.component.ComponentAutowired;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.invoice.InvoiceId;
import pico.erp.invoice.InvoiceService;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.notify.type.NotifyTypeDefinition;
import pico.erp.notify.type.NotifyTypeId;
import pico.erp.rest.config.notify.NotifyContextFactory;
import pico.erp.user.UserService;

@ComponentBean(host = false)
@Component
public class InvoiceCreatedNotifyTypeDefinition implements NotifyTypeDefinition<InvoiceId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("invoice-created");

  @ComponentAutowired
  private InvoiceService invoiceService;

  @ComponentAutowired
  private CompanyService companyService;

  @ComponentAutowired
  private UserService userService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Override
  public Object createContext(InvoiceId key) {
    val context = contextFactory.factory();
    val data = context.getData();
    val invoice = invoiceService.get(key);
    data.put("receiver", companyService.get(invoice.getReceiverId()));
    data.put("sender", companyService.get(invoice.getSenderId()));
    data.put("invoice", invoice);
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
    return InvoiceNotifySubjectTypeDefinition.ID;
  }
}
