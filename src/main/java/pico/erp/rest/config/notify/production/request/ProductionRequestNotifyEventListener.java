package pico.erp.rest.config.notify.production.request;

import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.notify.NotifyRequests;
import pico.erp.notify.NotifyService;
import pico.erp.production.plan.ProductionPlanProperties;
import pico.erp.production.request.ProductionRequestEvents;
import pico.erp.production.request.ProductionRequestProperties;
import pico.erp.production.request.ProductionRequestService;

@SuppressWarnings("unused")
@Component
public class ProductionRequestNotifyEventListener {

  private static final String LISTENER_NAME = "listener.notify-production-request-event-listener";

  @ComponentAutowired
  private ProductionRequestService productionRequestService;

  @ComponentAutowired
  private NotifyService notifyService;

  @ComponentAutowired
  private ProductionRequestProperties productionRequestProperties;

  @ComponentAutowired
  private ProductionPlanProperties productionPlanProperties;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + ProductionRequestEvents.AcceptedEvent.CHANNEL)
  public void onProductionRequestAccepted(ProductionRequestEvents.AcceptedEvent event) {
    val id = event.getId();
    val productionRequest = productionRequestService.get(id);

    notifyService.notify(
      NotifyRequests.NotifyUserRequest.builder()
        .userId(productionRequest.getRequesterId())
        .typeId(ProductionRequestAcceptedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );

    notifyService.notify(
      NotifyRequests.NotifyGroupRequest.builder()
        .groupId(productionPlanProperties.getChargerGroup().getId())
        .typeId(ProductionRequestAwaitPlanNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );

  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + ProductionRequestEvents.CommittedEvent.CHANNEL)
  public void onProductionRequestCommitted(ProductionRequestEvents.CommittedEvent event) {
    val id = event.getId();
    notifyService.notify(
      NotifyRequests.NotifyGroupRequest.builder()
        .groupId(productionRequestProperties.getAccepterGroup().getId())
        .typeId(ProductionRequestCommittedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + ProductionRequestEvents.CompletedEvent.CHANNEL)
  public void onProductionRequestCompleted(ProductionRequestEvents.CompletedEvent event) {
    val id = event.getId();
    val productionRequest = productionRequestService.get(id);

    notifyService.notify(
      NotifyRequests.NotifyUserRequest.builder()
        .userId(productionRequest.getRequesterId())
        .typeId(ProductionRequestCompletedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

}
