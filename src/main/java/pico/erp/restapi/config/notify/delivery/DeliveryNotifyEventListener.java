package pico.erp.restapi.config.notify.delivery;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;
import pico.erp.delivery.result.DeliveryResultEvents;
import pico.erp.delivery.result.DeliveryResultService;
import pico.erp.notify.NotifyRequests;
import pico.erp.notify.NotifyService;
import pico.erp.purchase.request.PurchaseRequestEvents;
import pico.erp.purchase.request.PurchaseRequestProperties;
import pico.erp.purchase.request.PurchaseRequestService;
import pico.erp.restapi.config.notify.purchase.request.PurchaseRequestCommittedNotifyTypeDefinition;
import pico.erp.restapi.config.notify.purchase.request.PurchaseRequestCompletedNotifyTypeDefinition;
import pico.erp.restapi.config.notify.purchase.request.PurchaseRequestRejectedNotifyTypeDefinition;

@SuppressWarnings("unused")
@Component
public class DeliveryNotifyEventListener {

  private static final String LISTENER_NAME = "listener.delivery-notify-event-listener";

  @Lazy
  @Autowired
  private PurchaseRequestService purchaseRequestService;

  @Lazy
  @Autowired
  private NotifyService notifyService;

  @Lazy
  @Autowired
  private PurchaseRequestProperties purchaseRequestProperties;

  @Lazy
  @Autowired
  private DeliveryResultService deliveryResultService;

  @Autowired
  private ErrorHandler errorHandler;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + DeliveryResultEvents.ErrorOccurredEvent.CHANNEL)
  public void onDeliveryErrorOccurred(DeliveryResultEvents.ErrorOccurredEvent event) {
    val id = event.getId();
    val result = deliveryResultService.get(event.getId());

    errorHandler.handleError(new Exception(event.getStacktrace()));

    notifyService.notify(
      NotifyRequests.NotifyUserRequest.builder()
        .userId(result.getRequesterId())
        .typeId(DeliveryErrorOccurredNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseRequestEvents.CommittedEvent.CHANNEL)
  public void onPurchaseRequestCommitted(PurchaseRequestEvents.CommittedEvent event) {
    val id = event.getId();
    notifyService.notify(
      NotifyRequests.NotifyGroupRequest.builder()
        .groupId(purchaseRequestProperties.getAccepterGroup().getId())
        .typeId(PurchaseRequestCommittedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseRequestEvents.CompletedEvent.CHANNEL)
  public void onPurchaseRequestCompleted(PurchaseRequestEvents.CompletedEvent event) {
    val id = event.getId();
    val purchaseRequest = purchaseRequestService.get(id);

    notifyService.notify(
      NotifyRequests.NotifyUserRequest.builder()
        .userId(purchaseRequest.getRequesterId())
        .typeId(PurchaseRequestCompletedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseRequestEvents.RejectedEvent.CHANNEL)
  public void onPurchaseRequestRejected(PurchaseRequestEvents.RejectedEvent event) {
    val id = event.getId();
    val purchaseRequest = purchaseRequestService.get(id);

    notifyService.notify(
      NotifyRequests.NotifyUserRequest.builder()
        .userId(purchaseRequest.getRequesterId())
        .typeId(PurchaseRequestRejectedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

}
