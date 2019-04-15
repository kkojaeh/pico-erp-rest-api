package pico.erp.rest.config.notify.production.order;

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
import pico.erp.process.ProcessService;
import pico.erp.production.order.ProductionOrderId;
import pico.erp.production.order.ProductionOrderService;
import pico.erp.rest.config.notify.NotifyContextFactory;

@ComponentBean(host = false)
@Component
public class ProductionOrderCommittedNotifyTypeDefinition implements
  NotifyTypeDefinition<ProductionOrderId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("production-order-committed");

  @ComponentAutowired
  private ProductionOrderService productionOrderService;

  @ComponentAutowired
  private CompanyService companyService;

  @ComponentAutowired
  private ItemService itemService;

  @ComponentAutowired
  private ProcessService processService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Override
  public Object createContext(ProductionOrderId key) {
    val context = contextFactory.factory();
    val data = context.getData();
    val order = productionOrderService.get(key);
    val item = itemService.get(order.getItemId());
    val receiver = companyService.get(order.getReceiverId());
    val process = processService.get(order.getProcessId());
    data.put("order", order);
    data.put("item", item);
    data.put("process", process);
    data.put("receiver", receiver);
    return context;
  }

  @Override
  public ProductionOrderId createKey(String key) {
    return ProductionOrderId.from(key);
  }

  @Override
  public NotifyTypeId getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "생산 지시 제출 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return ProductionOrderNotifySubjectTypeDefinition.ID;
  }
}
