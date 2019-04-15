package pico.erp.rest.config;

import java.util.Optional;
import kkojaeh.spring.boot.component.ComponentAutowired;
import kkojaeh.spring.boot.component.ComponentBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import pico.erp.comment.CommentEvents.MentionedEvent;
import pico.erp.user.UserData;
import pico.erp.user.UserService;

@Configuration
public class CommentConfiguration {

  private static final String LISTENER_NAME = "listener.comment-configuration";

  @ComponentAutowired
  private UserService userService;

  @ComponentBean(host = false)
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
