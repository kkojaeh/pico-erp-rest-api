package pico.erp.restapi.config.notify.fax;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.fax.FaxEvents;
import pico.erp.fax.FaxService;
import pico.erp.notify.NotifyRequests;
import pico.erp.notify.NotifyService;

@SuppressWarnings("unused")
@Component
public class FaxNotifyEventListener {

  private static final String LISTENER_NAME = "listener.fax-notify-event-listener";

  @Lazy
  @Autowired
  private NotifyService notifyService;

  @Lazy
  @Autowired
  private FaxService faxService;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + FaxEvents.FailedEvent.CHANNEL)
  public void onFaxFailed(FaxEvents.FailedEvent event) {
    val id = event.getId();
    val fax = faxService.get(id);
    notifyService.notify(
      NotifyRequests.NotifyUserRequest.builder()
        .userId(fax.getRequesterId())
        .typeId(FaxFailedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + FaxEvents.SentEvent.CHANNEL)
  public void onFaxSent(FaxEvents.SentEvent event) {
    val id = event.getId();
    val fax = faxService.get(id);
    notifyService.notify(
      NotifyRequests.NotifyUserRequest.builder()
        .userId(fax.getRequesterId())
        .typeId(FaxSentNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

}
