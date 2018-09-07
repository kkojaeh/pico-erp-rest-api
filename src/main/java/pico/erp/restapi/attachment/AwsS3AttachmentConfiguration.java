package pico.erp.restapi.attachment;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "attachment.aws.s3")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AwsS3AttachmentConfiguration {

  @Getter
  @Setter
  String bucketName;

  @Setter
  Regions region = Regions.AP_NORTHEAST_1;

  @Bean
  public AmazonS3 amazonS3Client() {
    return AmazonS3ClientBuilder.standard()
      .withRegion(region)
      .withCredentials(new EnvironmentVariableCredentialsProvider()).build();
  }

}
