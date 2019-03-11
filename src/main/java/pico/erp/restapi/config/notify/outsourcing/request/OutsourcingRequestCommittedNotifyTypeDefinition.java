package pico.erp.restapi.config.notify.outsourcing.request;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.notify.type.NotifyTypeDefinition;
import pico.erp.notify.type.NotifyTypeId;
import pico.erp.outsourcing.request.OutsourcingRequestId;
import pico.erp.outsourcing.request.OutsourcingRequestService;
import pico.erp.restapi.config.notify.NotifyContextFactory;
import pico.erp.shared.Public;
import pico.erp.user.UserService;

@Public
@Component
public class OutsourcingRequestCommittedNotifyTypeDefinition implements
  NotifyTypeDefinition<OutsourcingRequestId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("outsourcing-request-committed");

  @Lazy
  @Autowired
  private OutsourcingRequestService outsourcingRequestService;

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Override
  public Object createContext(OutsourcingRequestId key) {
    val context = contextFactory.factory();
    val data = context.getData();
    val outsourcingRequest = outsourcingRequestService.get(key);
    val requester = userService.get(outsourcingRequest.getRequesterId());
    val receiver = companyService.get(outsourcingRequest.getReceiverId());
    data.put("outsourcingRequest", outsourcingRequest);
    data.put("requester", requester);
    data.put("receiver", receiver);
    return context;
  }

  @Override
  public OutsourcingRequestId createKey(String key) {
    return OutsourcingRequestId.from(key);
  }

  @Override
  public NotifyTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "외주 요청 제출 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return OutsourcingRequestNotifySubjectTypeDefinition.ID;
  }
}
