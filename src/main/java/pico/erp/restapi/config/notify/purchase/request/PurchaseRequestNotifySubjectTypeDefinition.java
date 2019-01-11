package pico.erp.restapi.config.notify.purchase.request;

import org.springframework.stereotype.Component;
import pico.erp.notify.subject.NotifySubjectId;
import pico.erp.notify.subject.type.NotifySubjectTypeDefinition;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.purchase.request.PurchaseRequestId;
import pico.erp.shared.Public;

@Public
@Component
public class PurchaseRequestNotifySubjectTypeDefinition implements
  NotifySubjectTypeDefinition<PurchaseRequestId> {

  public static final NotifySubjectTypeId ID = NotifySubjectTypeId.from("purchase-request");

  @Override
  public NotifySubjectId convert(PurchaseRequestId key) {
    return NotifySubjectId.from(getId(), key.getValue().toString());
  }

  @Override
  public NotifySubjectTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "구매 요청";
  }
}
