package pico.erp.restapi.config.notify.delivery;

import org.springframework.stereotype.Component;
import pico.erp.delivery.result.DeliveryResultId;
import pico.erp.notify.subject.NotifySubjectId;
import pico.erp.notify.subject.type.NotifySubjectTypeDefinition;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.shared.Public;

@Public
@Component
public class DeliveryResultNotifySubjectTypeDefinition implements
  NotifySubjectTypeDefinition<DeliveryResultId> {

  public static final NotifySubjectTypeId ID = NotifySubjectTypeId.from("delivery-result");

  @Override
  public NotifySubjectId convert(DeliveryResultId key) {
    return NotifySubjectId.from(getId(), key.getValue().toString());
  }

  @Override
  public NotifySubjectTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "전달 결과";
  }
}
