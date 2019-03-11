package pico.erp.restapi.config.notify.outsourcing.request;

import org.springframework.stereotype.Component;
import pico.erp.notify.subject.NotifySubjectId;
import pico.erp.notify.subject.type.NotifySubjectTypeDefinition;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.outsourcing.request.OutsourcingRequestId;
import pico.erp.shared.Public;

@Public
@Component
public class OutsourcingRequestNotifySubjectTypeDefinition implements
  NotifySubjectTypeDefinition<OutsourcingRequestId> {

  public static final NotifySubjectTypeId ID = NotifySubjectTypeId.from("outsourcing-request");

  @Override
  public NotifySubjectId convert(OutsourcingRequestId key) {
    return NotifySubjectId.from(getId(), key.getValue().toString());
  }

  @Override
  public NotifySubjectTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "외주 요청";
  }
}
