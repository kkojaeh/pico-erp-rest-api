package pico.erp.restapi.config.notify.invoice;

import org.springframework.stereotype.Component;
import pico.erp.invoice.InvoiceId;
import pico.erp.notify.subject.NotifySubjectId;
import pico.erp.notify.subject.type.NotifySubjectTypeDefinition;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.shared.Public;

@Public
@Component
public class InvoiceNotifySubjectTypeDefinition implements
  NotifySubjectTypeDefinition<InvoiceId> {

  public static final NotifySubjectTypeId ID = NotifySubjectTypeId.from("invoice");

  @Override
  public NotifySubjectId convert(InvoiceId key) {
    return NotifySubjectId.from(getId(), key.getValue().toString());
  }

  @Override
  public NotifySubjectTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "송장 수령";
  }
}
