package pico.erp.restapi.config.notify.purchase.request;

import java.util.HashMap;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.notify.type.NotifyTypeDefinition;
import pico.erp.notify.type.NotifyTypeId;
import pico.erp.purchase.request.PurchaseRequestId;
import pico.erp.purchase.request.PurchaseRequestService;
import pico.erp.restapi.ClientProperties;
import pico.erp.shared.Public;
import pico.erp.user.UserService;

@Public
@Component
public class PurchaseRequestCommittedNotifyTypeDefinition implements
  NotifyTypeDefinition<PurchaseRequestId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("purchase-request-committed");

  @Lazy
  @Autowired
  private PurchaseRequestService purchaseRequestService;

  @Lazy
  @Autowired
  private UserService userService;

  @Autowired
  private ClientProperties clientProperties;

  @Override
  public Object createContext(PurchaseRequestId key) {
    val context = new HashMap<String, Object>();
    val purchaseRequest = purchaseRequestService.get(key);
    val requester = userService.get(purchaseRequest.getRequesterId());
    context.put("purchaseRequest", purchaseRequest);
    context.put("requester", requester);
    context.put("locationOrigin", clientProperties.getLocationOrigin());
    return context;
  }

  @Override
  public NotifyTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "구매 요청 제출 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return PurchaseRequestNotifySubjectTypeDefinition.ID;
  }
}
