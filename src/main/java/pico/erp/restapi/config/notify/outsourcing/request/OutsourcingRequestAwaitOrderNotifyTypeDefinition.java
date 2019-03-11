package pico.erp.restapi.config.notify.outsourcing.request;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.item.ItemService;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.notify.type.NotifyTypeDefinition;
import pico.erp.notify.type.NotifyTypeId;
import pico.erp.outsourcing.request.OutsourcingRequestId;
import pico.erp.outsourcing.request.OutsourcingRequestService;
import pico.erp.process.ProcessService;
import pico.erp.restapi.config.notify.NotifyContextFactory;
import pico.erp.shared.Public;
import pico.erp.user.UserService;

@Public
@Component
public class OutsourcingRequestAwaitOrderNotifyTypeDefinition implements
  NotifyTypeDefinition<OutsourcingRequestId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("outsourcing-request-await-order");

  @Lazy
  @Autowired
  private OutsourcingRequestService outsourcingRequestService;

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Lazy
  @Autowired
  private ItemService itemService;

  @Lazy
  @Autowired
  private ProcessService processService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Override
  public Object createContext(OutsourcingRequestId key) {
    val context = contextFactory.factory();
    val data = context.getData();
    val request = outsourcingRequestService.get(key);
    val requester = userService.get(request.getRequesterId());
    val receiver = companyService.get(request.getReceiverId());
    val item = itemService.get(request.getItemId());
    val process = processService.get(request.getProcessId());
    data.put("request", request);
    data.put("item", item);
    data.put("requester", requester);
    data.put("receiver", receiver);
    data.put("process", process);
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
    return "외주 요청 발주 대기 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return OutsourcingRequestNotifySubjectTypeDefinition.ID;
  }
}
