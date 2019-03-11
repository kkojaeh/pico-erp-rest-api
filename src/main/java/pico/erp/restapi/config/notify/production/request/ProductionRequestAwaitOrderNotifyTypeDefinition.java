package pico.erp.restapi.config.notify.production.request;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pico.erp.company.CompanyService;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.notify.type.NotifyTypeDefinition;
import pico.erp.notify.type.NotifyTypeId;
import pico.erp.production.request.ProductionRequestId;
import pico.erp.production.request.ProductionRequestService;
import pico.erp.restapi.config.notify.NotifyContextFactory;
import pico.erp.shared.Public;
import pico.erp.user.UserService;

@Public
@Component
public class ProductionRequestAwaitOrderNotifyTypeDefinition implements
  NotifyTypeDefinition<ProductionRequestId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("production-request-await-order");

  @Lazy
  @Autowired
  private ProductionRequestService productionRequestService;

  @Lazy
  @Autowired
  private UserService userService;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Override
  public Object createContext(ProductionRequestId key) {
    val context = contextFactory.factory();
    val data = context.getData();
    val productionRequest = productionRequestService.get(key);
    val requester = userService.get(productionRequest.getRequesterId());
    val receiver = companyService.get(productionRequest.getReceiverId());
    data.put("productionRequest", productionRequest);
    data.put("requester", requester);
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
    return "생산 요청 발주 대기 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return ProductionRequestNotifySubjectTypeDefinition.ID;
  }
}
