package pico.erp.rest.config.notify.fax;

import kkojaeh.spring.boot.component.ComponentAutowired;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pico.erp.fax.FaxId;
import pico.erp.fax.FaxService;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.notify.type.NotifyTypeDefinition;
import pico.erp.notify.type.NotifyTypeId;
import pico.erp.rest.config.notify.NotifyContextFactory;

@ComponentBean(host = false)
@Component
public class FaxFailedNotifyTypeDefinition implements
  NotifyTypeDefinition<FaxId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("fax-failed");

  @ComponentAutowired
  private FaxService faxService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Override
  public Object createContext(FaxId key) {
    val context = contextFactory.factory();
    val data = context.getData();
    val fax = faxService.get(key);
    data.put("fax", fax);
    return context;
  }

  @Override
  public FaxId createKey(String key) {
    return FaxId.from(key);
  }

  @Override
  public NotifyTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "FAX 실패 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return FaxNotifySubjectTypeDefinition.ID;
  }
}
