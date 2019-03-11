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

@Public
@Component
public class ProductionRequestRejectedNotifyTypeDefinition implements
  NotifyTypeDefinition<ProductionRequestId, Object> {

  public static final NotifyTypeId ID = NotifyTypeId.from("production-request-rejected");

  @Lazy
  @Autowired
  private ProductionRequestService productionRequestService;

  @Autowired
  private NotifyContextFactory contextFactory;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Override
  public Object createContext(ProductionRequestId key) {
    val context = contextFactory.factory();
    val data = context.getData();
    val productionRequest = productionRequestService.get(key);
    val receiver = companyService.get(productionRequest.getReceiverId());
    data.put("productionRequest", productionRequest);
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
    return "생산 요청 반려 알림";
  }

  @Override
  public NotifySubjectTypeId getSubjectTypeId() {
    return ProductionRequestNotifySubjectTypeDefinition.ID;
  }
}
