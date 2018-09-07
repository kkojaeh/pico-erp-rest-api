package pico.erp.restapi.attachment;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pico.erp.attachment.AttachmentStorageStrategy;
import pico.erp.attachment.impl.FileSystemAttachmentStorageStrategy;
import pico.erp.shared.Public;

@Configuration
public class AttachmentConfiguration {

  @Public
  @Bean
  @Profile({"production", "development"})
  public AttachmentStorageStrategy awsS3AttachmentItemPersistStrategy() {
    return new AwsS3AttachmentStorageStrategy();
  }

  @Public
  @Bean
  @ConditionalOnMissingBean(AttachmentStorageStrategy.class)
  public AttachmentStorageStrategy fileSystemAttachmentItemPersistStrategy() {
    return new FileSystemAttachmentStorageStrategy();
  }


}
