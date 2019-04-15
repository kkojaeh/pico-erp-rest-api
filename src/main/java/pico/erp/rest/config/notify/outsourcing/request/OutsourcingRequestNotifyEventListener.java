package pico.erp.rest.config.notify.outsourcing.request;

import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.notify.NotifyRequests;
import pico.erp.notify.NotifyService;
import pico.erp.outsourcing.order.OutsourcingOrderProperties;
import pico.erp.outsourcing.request.OutsourcingRequestEvents;
import pico.erp.outsourcing.request.OutsourcingRequestProperties;
import pico.erp.outsourcing.request.OutsourcingRequestService;

@SuppressWarnings("unused")
@Component
public class OutsourcingRequestNotifyEventListener {

  private static final String LISTENER_NAME = "listener.notify-outsourcing-request-event-listener";

  @ComponentAutowired
  private OutsourcingRequestService outsourcingRequestService;

  @ComponentAutowired
  private NotifyService notifyService;

  @ComponentAutowired
  private OutsourcingRequestProperties outsourcingRequestProperties;

  @ComponentAutowired
  private OutsourcingOrderProperties outsourcingOrderProperties;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingRequestEvents.AcceptedEvent.CHANNEL)
  public void onOutsourcingRequestAccepted(OutsourcingRequestEvents.AcceptedEvent event) {
    val id = event.getId();
    val outsourcingRequest = outsourcingRequestService.get(id);

    notifyService.notify(
      NotifyRequests.NotifyUserRequest.builder()
        .userId(outsourcingRequest.getRequesterId())
        .typeId(OutsourcingRequestAcceptedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );

    notifyService.notify(
      NotifyRequests.NotifyGroupRequest.builder()
        .groupId(outsourcingOrderProperties.getChargerGroup().getId())
        .typeId(OutsourcingRequestAwaitOrderNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingRequestEvents.CommittedEvent.CHANNEL)
  public void onOutsourcingRequestCommitted(OutsourcingRequestEvents.CommittedEvent event) {
    val id = event.getId();
    notifyService.notify(
      NotifyRequests.NotifyGroupRequest.builder()
        .groupId(outsourcingRequestProperties.getAccepterGroup().getId())
        .typeId(OutsourcingRequestCommittedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingRequestEvents.CompletedEvent.CHANNEL)
  public void onOutsourcingRequestCompleted(OutsourcingRequestEvents.CompletedEvent event) {
    val id = event.getId();
    val outsourcingRequest = outsourcingRequestService.get(id);

    notifyService.notify(
      NotifyRequests.NotifyUserRequest.builder()
        .userId(outsourcingRequest.getRequesterId())
        .typeId(OutsourcingRequestCompletedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + OutsourcingRequestEvents.RejectedEvent.CHANNEL)
  public void onOutsourcingRequestRejected(OutsourcingRequestEvents.RejectedEvent event) {
    val id = event.getId();
    val outsourcingRequest = outsourcingRequestService.get(id);

    notifyService.notify(
      NotifyRequests.NotifyUserRequest.builder()
        .userId(outsourcingRequest.getRequesterId())
        .typeId(OutsourcingRequestRejectedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

}
