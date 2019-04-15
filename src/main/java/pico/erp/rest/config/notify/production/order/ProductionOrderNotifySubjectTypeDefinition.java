package pico.erp.rest.config.notify.production.order;

import kkojaeh.spring.boot.component.ComponentBean;
import org.springframework.stereotype.Component;
import pico.erp.notify.subject.NotifySubjectId;
import pico.erp.notify.subject.type.NotifySubjectTypeDefinition;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.production.order.ProductionOrderId;

@ComponentBean(host = false)
@Component
public class ProductionOrderNotifySubjectTypeDefinition implements
  NotifySubjectTypeDefinition<ProductionOrderId> {

  public static final NotifySubjectTypeId ID = NotifySubjectTypeId.from("production-order");

  @Override
  public NotifySubjectId convert(ProductionOrderId key) {
    return NotifySubjectId.from(getId(), key.getValue().toString());
  }

  @Override
  public NotifySubjectTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "생산 지시";
  }
}
