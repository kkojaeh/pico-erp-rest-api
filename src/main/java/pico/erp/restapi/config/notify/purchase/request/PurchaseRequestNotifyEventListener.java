package pico.erp.restapi.config.notify.purchase.request;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.notify.NotifyRequests;
import pico.erp.notify.NotifyService;
import pico.erp.purchase.order.PurchaseOrderProperties;
import pico.erp.purchase.request.PurchaseRequestEvents;
import pico.erp.purchase.request.PurchaseRequestProperties;
import pico.erp.purchase.request.PurchaseRequestService;

@SuppressWarnings("unused")
@Component
public class PurchaseRequestNotifyEventListener {

  private static final String LISTENER_NAME = "listener.notify-purchase-request-event-listener";

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
  private PurchaseOrderProperties purchaseOrderProperties;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseRequestEvents.AcceptedEvent.CHANNEL)
  public void onPurchaseRequestAccepted(PurchaseRequestEvents.AcceptedEvent event) {
    val id = event.getPurchaseRequestId();
    val purchaseRequest = purchaseRequestService.get(id);

    notifyService.notify(
      NotifyRequests.NotifyUserRequest.builder()
        .userId(purchaseRequest.getRequesterId())
        .typeId(PurchaseRequestAcceptedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );

    notifyService.notify(
      NotifyRequests.NotifyGroupRequest.builder()
        .groupId(purchaseOrderProperties.getChargerGroup().getId())
        .typeId(PurchaseRequestAwaitOrderNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + PurchaseRequestEvents.CommittedEvent.CHANNEL)
  public void onPurchaseRequestCommitted(PurchaseRequestEvents.CommittedEvent event) {
    val id = event.getPurchaseRequestId();
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
    val id = event.getPurchaseRequestId();
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
    val id = event.getPurchaseRequestId();
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
