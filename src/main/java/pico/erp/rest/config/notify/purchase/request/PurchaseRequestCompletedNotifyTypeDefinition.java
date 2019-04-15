package pico.erp.rest.config.notify.purchase.request;

import kkojaeh.spring.boot.component.ComponentAutowired;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.item.ItemService;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.notify.type.NotifyTypeDefinition;
import pico.erp.notify.type.NotifyTypeId;
import pico.erp.purchase.request.PurchaseRequestId;
import pico.erp.purchase.request.PurchaseRequestService;
import pico.erp.rest.config.notify.NotifyContextFactory;

@ComponentBean(host = false)
@Component
public class PurchaseRequestCompletedNotifyTypeDefinition implements
  NotifyTypeDefinition<PurchaseRequestId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("purchase-request-completed");

  @ComponentAutowired
  private PurchaseRequestService purchaseRequestService;

  @ComponentAutowired
  private CompanyService companyService;

  @ComponentAutowired
  private ItemService itemService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Override
  public Object createContext(PurchaseRequestId key) {
    val context = contextFactory.factory();
    val data = context.getData();
    val request = purchaseRequestService.get(key);
    val receiver = companyService.get(request.getReceiverId());
    val item = itemService.get(request.getItemId());
    data.put("request", request);
    data.put("item", item);
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
    return "구매 요청 완료 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return PurchaseRequestNotifySubjectTypeDefinition.ID;
  }
}
