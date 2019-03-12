package pico.erp.restapi.config.notify.production.order;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.notify.NotifyRequests;
import pico.erp.notify.NotifyService;
import pico.erp.production.execution.ProductionExecutionProperties;
import pico.erp.production.order.ProductionOrderEvents;

@SuppressWarnings("unused")
@Component
public class ProductionOrderNotifyEventListener {

  private static final String LISTENER_NAME = "listener.notify-production-order-event-listener";

  @Lazy
  @Autowired
  private NotifyService notifyService;

  @Lazy
  @Autowired
  private ProductionExecutionProperties productionExecutionProperties;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + ProductionOrderEvents.PreparedEvent.CHANNEL)
  public void onProductionOrderPrepared(ProductionOrderEvents.PreparedEvent event) {
    val id = event.getId();

    notifyService.notify(
      NotifyRequests.NotifyGroupRequest.builder()
        .groupId(productionExecutionProperties.getExecutorGroup().getId())
        .typeId(ProductionOrderPreparedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

}
