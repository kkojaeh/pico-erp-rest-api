package pico.erp.rest.config.notify.production.order;

import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.notify.NotifyRequests;
import pico.erp.notify.NotifyService;
import pico.erp.production.execution.ProductionExecutionProperties;
import pico.erp.production.order.ProductionOrderEvents;
import pico.erp.production.order.ProductionOrderProperties;

@SuppressWarnings("unused")
@Component
public class ProductionOrderNotifyEventListener {

  private static final String LISTENER_NAME = "listener.notify-production-order-event-listener";

  @ComponentAutowired
  private NotifyService notifyService;

  @ComponentAutowired
  private ProductionOrderProperties productionOrderProperties;

  @ComponentAutowired
  private ProductionExecutionProperties productionExecutionProperties;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + ProductionOrderEvents.CommittedEvent.CHANNEL)
  public void onProductionOrderCommitted(ProductionOrderEvents.CommittedEvent event) {
    val id = event.getId();
    notifyService.notify(
      NotifyRequests.NotifyGroupRequest.builder()
        .groupId(productionOrderProperties.getAccepterGroup().getId())
        .typeId(ProductionOrderCommittedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

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
