package pico.erp.restapi.config.notify.invoice;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.invoice.InvoiceEvents;
import pico.erp.invoice.InvoiceProperties;
import pico.erp.invoice.InvoiceService;
import pico.erp.notify.NotifyRequests;
import pico.erp.notify.NotifyService;

@SuppressWarnings("unused")
@Component
public class InvoiceNotifyEventListener {

  private static final String LISTENER_NAME = "listener.notify-invoice-event-listener";

  @Lazy
  @Autowired
  private InvoiceService invoiceService;

  @Lazy
  @Autowired
  private NotifyService notifyService;

  @Lazy
  @Autowired
  private InvoiceProperties invoiceProperties;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "."
    + InvoiceEvents.CreatedEvent.CHANNEL)
  public void onInvoiceCreated(InvoiceEvents.CreatedEvent event) {
    val id = event.getId();

    notifyService.notify(
      NotifyRequests.NotifyGroupRequest.builder()
        .groupId(invoiceProperties.getReceiverGroup().getId())
        .typeId(InvoiceCreatedNotifyTypeDefinition.ID)
        .key(id)
        .build()
    );
  }

}
