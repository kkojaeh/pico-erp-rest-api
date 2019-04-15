package pico.erp.rest.config.notify.fax;

import kkojaeh.spring.boot.component.ComponentBean;
import org.springframework.stereotype.Component;
import pico.erp.fax.FaxId;
import pico.erp.notify.subject.NotifySubjectId;
import pico.erp.notify.subject.type.NotifySubjectTypeDefinition;
import pico.erp.notify.subject.type.NotifySubjectTypeId;

@ComponentBean(host = false)
@Component
public class FaxNotifySubjectTypeDefinition implements
  NotifySubjectTypeDefinition<FaxId> {

  public static final NotifySubjectTypeId ID = NotifySubjectTypeId.from("fax");

  @Override
  public NotifySubjectId convert(FaxId key) {
    return NotifySubjectId.from(getId(), key.getValue().toString());
  }

  @Override
  public NotifySubjectTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "FAX";
  }
}
