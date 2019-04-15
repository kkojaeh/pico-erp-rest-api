package pico.erp.rest.config.notify.production.request;

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
import pico.erp.production.request.ProductionRequestId;
import pico.erp.production.request.ProductionRequestService;
import pico.erp.rest.config.notify.NotifyContextFactory;
import pico.erp.user.UserService;

@ComponentBean(host = false)
@Component
public class ProductionRequestAwaitPlanNotifyTypeDefinition implements
  NotifyTypeDefinition<ProductionRequestId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("production-request-await-plan");

  @ComponentAutowired
  private ProductionRequestService productionRequestService;

  @ComponentAutowired
  private UserService userService;

  @ComponentAutowired
  private CompanyService companyService;

  @ComponentAutowired
  private ItemService itemService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Override
  public Object createContext(ProductionRequestId key) {
    val context = contextFactory.factory();
    val data = context.getData();
    val request = productionRequestService.get(key);
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
  public ProductionRequestId createKey(String key) {
    return ProductionRequestId.from(key);
  }

  @Override
  public NotifyTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "생산 요청 계획 대기 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return ProductionRequestNotifySubjectTypeDefinition.ID;
  }
}
