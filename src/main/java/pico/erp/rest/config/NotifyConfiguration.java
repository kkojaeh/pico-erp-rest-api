package pico.erp.rest.config;

import kkojaeh.spring.boot.component.ComponentBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pico.erp.notify.message.NotifyMessage;
import pico.erp.notify.sender.MattermostNotifySenderDefinition;
import pico.erp.notify.sender.MattermostNotifySenderDefinitionConfig;
import pico.erp.notify.sender.NotifySenderDefinition;
import pico.erp.notify.sender.NotifySenderId;
import pico.erp.notify.target.NotifyGroupData;
import pico.erp.notify.target.NotifyTargetData;

@Configuration
public class NotifyConfiguration {

  @Bean
  @ConfigurationProperties("notify.mattermost")
  public MattermostNotifySenderDefinitionConfig config() {
    return new MattermostNotifySenderDefinitionConfig();
  }

  @ComponentBean(host = false)
  @Bean
  @Profile({"development", "production"})
  public NotifySenderDefinition mattermostNotifySenderDefinition() {
    return new MattermostNotifySenderDefinition(config());
  }

  @ComponentBean(host = false)
  @Bean
  @ConditionalOnMissingBean(NotifySenderDefinition.class)
  public NotifySenderDefinition noOpNotifySenderDefinition() {
    return new NotifySenderDefinition() {

      @Override
      public NotifySenderId getId() {
        return NotifySenderId.from("console");
      }

      @Override
      public String getName() {
        return "console";
      }

      @Override
      public boolean send(NotifyMessage message, NotifyGroupData group) {
        System.out.println(message.asMarkdown());
        return true;
      }

      @Override
      public boolean send(NotifyMessage message, NotifyTargetData target) {
        System.out.println(message.asMarkdown());
        return true;
      }
    };
  }


}
