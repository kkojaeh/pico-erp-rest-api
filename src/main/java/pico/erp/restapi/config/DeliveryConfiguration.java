package pico.erp.restapi.config;

import lombok.Setter;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import pico.erp.delivery.TwilioAwsS3FaxDeliverySendService;
import pico.erp.delivery.message.DeliveryMessage;
import pico.erp.delivery.send.DeliverySendRequests.SendRequest;
import pico.erp.delivery.send.FaxDeliverySendService;
import pico.erp.delivery.send.MailDeliverySendService;
import pico.erp.delivery.send.SpringMailDeliverySendService;
import pico.erp.shared.Public;

@Configuration
@ConfigurationProperties(prefix = "delivery")
public class DeliveryConfiguration {

  @Setter
  String fromEmail;

  @Setter
  String fromName;

  @Public
  @Bean
  @ConditionalOnMissingBean(MailDeliverySendService.class)
  public MailDeliverySendService noOpMailDeliverySendService() {
    return new MailDeliverySendService() {

      @Override
      public void send(SendRequest request, DeliveryMessage message) {
        System.out.println("send mail to : " + request.getAddress());
      }
    };
  }

  @Public
  @Bean
  @Profile({"production", "development"})
  public MailDeliverySendService springMailDeliverySendService(JavaMailSender sender) {
    val config = SpringMailDeliverySendService.Config.builder()
      .sender(sender)
      .fromEmail(fromEmail)
      .fromName(fromName)
      .build();
    return new SpringMailDeliverySendService(config);
  }

  @Public
  @Bean
  public FaxDeliverySendService twilioAwsS3FaxDeliverySendService() {
    return new TwilioAwsS3FaxDeliverySendService();
  }

}