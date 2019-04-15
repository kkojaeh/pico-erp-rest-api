package pico.erp.rest.config;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.io.File;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.Setter;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pico.erp.attachment.storage.AttachmentStorageStrategy;
import pico.erp.attachment.storage.AwsS3AttachmentStorageStrategy;
import pico.erp.attachment.storage.FileSystemAttachmentStorageStrategy;

@Configuration
@ConfigurationProperties(prefix = "attachment.aws-s3")
public class AttachmentConfiguration {

  @Setter
  String bucketName;

  @Setter
  Regions region;

  @ComponentBean(host = false)
  @Bean
  @Profile({"production", "development"})
  public AttachmentStorageStrategy awsS3AttachmentItemPersistStrategy() {
    val amazonS3 = AmazonS3ClientBuilder.standard()
      .withRegion(region)
      .withCredentials(new EnvironmentVariableCredentialsProvider()).build();
    val config = AwsS3AttachmentStorageStrategy.Config.builder()
      .amazonS3(amazonS3)
      .amazonS3BucketName(bucketName)
      .build();
    return new AwsS3AttachmentStorageStrategy(config);
  }

  @ComponentBean(host = false)
  @Bean
  @ConditionalOnMissingBean(AttachmentStorageStrategy.class)
  public AttachmentStorageStrategy fileSystemAttachmentItemPersistStrategy(
    @Value("${attachment.storage.root-dir}") File rootDirectory) {
    val config = FileSystemAttachmentStorageStrategy.Config.builder()
      .rootDirectory(rootDirectory)
      .build();
    return new FileSystemAttachmentStorageStrategy(config);
  }


}
