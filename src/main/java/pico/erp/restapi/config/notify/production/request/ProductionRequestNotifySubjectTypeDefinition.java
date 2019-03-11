package pico.erp.restapi.config.notify.production.request;

import org.springframework.stereotype.Component;
import pico.erp.notify.subject.NotifySubjectId;
import pico.erp.notify.subject.type.NotifySubjectTypeDefinition;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.production.request.ProductionRequestId;
import pico.erp.shared.Public;

@Public
@Component
public class ProductionRequestNotifySubjectTypeDefinition implements
  NotifySubjectTypeDefinition<ProductionRequestId> {

  public static final NotifySubjectTypeId ID = NotifySubjectTypeId.from("production-request");

  @Override
  public NotifySubjectId convert(ProductionRequestId key) {
    return NotifySubjectId.from(getId(), key.getValue().toString());
  }

  @Override
  public NotifySubjectTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "생산 요청";
  }
}
