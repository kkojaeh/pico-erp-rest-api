package pico.erp.restapi.config.notify.purchase.request;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.item.ItemService;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.notify.type.NotifyTypeDefinition;
import pico.erp.notify.type.NotifyTypeId;
import pico.erp.purchase.request.PurchaseRequestId;
import pico.erp.purchase.request.PurchaseRequestService;
import pico.erp.restapi.config.notify.NotifyContextFactory;
import pico.erp.shared.Public;
import pico.erp.user.UserService;

@Public
@Component
public class PurchaseRequestAcceptedNotifyTypeDefinition implements
  NotifyTypeDefinition<PurchaseRequestId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("purchase-request-accepted");

  @Lazy
  @Autowired
  private PurchaseRequestService purchaseRequestService;

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Lazy
  @Autowired
  private ItemService itemService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Override
  public Object createContext(PurchaseRequestId key) {
    val context = contextFactory.factory();
    val data = context.getData();
    val request = purchaseRequestService.get(key);
    val accepter = userService.get(request.getAccepterId());
    val receiver = companyService.get(request.getReceiverId());
    val item = itemService.get(request.getItemId());
    data.put("request", request);
    data.put("item", item);
    data.put("accepter", accepter);
    data.put("receiver", receiver);
    return context;
  }

  @Override
  public PurchaseRequestId createKey(String key) {
    return PurchaseRequestId.from(key);
  }

  @Override
  public NotifyTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "구매 요청 접수 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return PurchaseRequestNotifySubjectTypeDefinition.ID;
  }
}
