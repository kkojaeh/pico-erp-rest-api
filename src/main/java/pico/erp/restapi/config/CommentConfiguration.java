package pico.erp.restapi.config;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import pico.erp.comment.CommentEvents.MentionedEvent;
import pico.erp.shared.Public;
import pico.erp.user.UserService;
import pico.erp.user.data.UserData;

@Configuration
public class CommentConfiguration {

  private static final String LISTENER_NAME = "listener.comment-configuration";

  @Lazy
  @Autowired
  private UserService userService;

  @Public
  @EventListener
  @JmsListener(destination = LISTENER_NAME + "." + MentionedEvent.CHANNEL)
  public void onCommentMentioned(MentionedEvent event) {
    UserData userData = Optional.of(event.getMention())
      .filter(mention -> mention.length() > 1) // 2자리 이상
      .map(userService::get) // 사용자 조회
      .orElse(null);
    // TODO: 언급 했음을 메세지 전송
  }

}
