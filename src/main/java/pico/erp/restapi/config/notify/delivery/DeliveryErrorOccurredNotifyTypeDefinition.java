package pico.erp.restapi.config.notify.delivery;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pico.erp.delivery.DeliveryService;
import pico.erp.delivery.result.DeliveryResultId;
import pico.erp.delivery.result.DeliveryResultService;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.notify.type.NotifyTypeDefinition;
import pico.erp.notify.type.NotifyTypeId;
import pico.erp.restapi.config.notify.NotifyContextFactory;
import pico.erp.shared.Public;

@Public
@Component
public class DeliveryErrorOccurredNotifyTypeDefinition implements
  NotifyTypeDefinition<DeliveryResultId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("delivery-error-occurred");

  @Lazy
  @Autowired
  private DeliveryResultService deliveryResultService;

  @Lazy
  @Autowired
  private DeliveryService deliveryService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Override
  public Object createContext(DeliveryResultId key) {
    val context = contextFactory.factory();
    val data = context.getData();
    val result = deliveryResultService.get(key);
    data.put("result", result);
    return context;
  }

  @Override
  public DeliveryResultId createKey(String key) {
    return DeliveryResultId.from(key);
  }

  @Override
  public NotifyTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "전달 오류 발생 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return DeliveryResultNotifySubjectTypeDefinition.ID;
  }
}
