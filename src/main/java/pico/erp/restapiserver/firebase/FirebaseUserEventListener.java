package pico.erp.restapiserver.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import pico.erp.user.UserEvents.CreatedEvent;
import pico.erp.user.UserEvents.DeletedEvent;
import pico.erp.user.UserEvents.UpdatedEvent;
import pico.erp.user.UserService;
import pico.erp.user.data.UserData;

@Component
public class FirebaseUserEventListener {

  private static final String LISTENER_NAME = "listener.firebase-user-event-listener";

  @Autowired
  private FirebaseAuth firebaseAuth;

  @Lazy
  @Autowired
  private UserService userService;

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "." + CreatedEvent.CHANNEL)
  public void onUserCreated(CreatedEvent event) {
    if (!userService.exists(event.getUserId())) {
      return;
    }
    UserData user = userService.get(event.getUserId());
    CreateRequest request = new CreateRequest()
      .setEmail(user.getEmail())
      .setEmailVerified(false)
      .setPassword(event.getPassword())
      .setDisplayName(user.getName())
      .setUid(user.getId().getValue())
      .setDisabled(!user.isEnabled());

    firebaseAuth.createUserAsync(request);
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "." + DeletedEvent.CHANNEL)
  public void onUserDeleted(DeletedEvent event) {
    firebaseAuth.deleteUserAsync(event.getUserId().getValue());
  }

  @EventListener
  @JmsListener(destination = LISTENER_NAME + "." + UpdatedEvent.CHANNEL)
  public void onUserUpdated(UpdatedEvent event) {
    UserData user = userService.get(event.getUserId());
    UpdateRequest request = new UpdateRequest(user.getId().getValue())
      .setEmail(user.getEmail())
      .setEmailVerified(false)
      .setDisplayName(user.getName())
      .setDisabled(!user.isEnabled());

    firebaseAuth.updateUserAsync(request);
  }

}
