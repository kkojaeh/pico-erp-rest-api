package pico.erp.rest.config;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pico.erp.fax.execute.FaxExecuteData;
import pico.erp.fax.execute.FaxExecuteId;
import pico.erp.fax.execute.FaxExecuteRequests.ClearRequest;
import pico.erp.fax.execute.FaxExecuteRequests.ExecuteRequest;
import pico.erp.fax.execute.FaxExecuteRequests.RetryRequest;
import pico.erp.fax.execute.FaxExecuteService;
import pico.erp.fax.execute.TwilioAwsS3FaxExecuteService;

@Configuration
@ConfigurationProperties(prefix = "fax")
public class FaxConfiguration {

  @Getter
  private AwsS3 awsS3 = new AwsS3();

  @Getter
  private Twilio twilio = new Twilio();

  @ComponentBean(host = false)
  @Bean
  @Profile({"production", "development"})
  public FaxExecuteService faxExecuteService() {
    val amazonS3 = AmazonS3ClientBuilder.standard()
      .withRegion(awsS3.region)
      .withCredentials(new EnvironmentVariableCredentialsProvider()).build();

    val config = TwilioAwsS3FaxExecuteService.Config.builder()
      .amazonS3(amazonS3)
      .amazonS3BucketName(awsS3.bucketName)
      .twilioAccountSid(twilio.accountSid)
      .twilioAuthToken(twilio.authToken)
      .twilioFaxFrom(twilio.faxFrom)
      .build();
    return new TwilioAwsS3FaxExecuteService(config);
  }

  @ComponentBean(host = false)
  @Bean
  @Profile({"default", "test"})
  public FaxExecuteService noOpFaxExecuteService() {
    return new FaxExecuteService() {

      FaxExecuteData data = FaxExecuteData.builder()
        .id(FaxExecuteId.from("test"))
        .completed(true)
        .processing(false)
        .failed(false)
        .build();

      @Override
      public void clear(ClearRequest request) {
      }

      @Override
      public FaxExecuteData execute(ExecuteRequest request) {
        return data;
      }

      @Override
      public FaxExecuteData get(FaxExecuteId id) {
        return data;
      }

      @Override
      public FaxExecuteData retry(RetryRequest request) {
        return data;
      }
    };
  }

  public static class AwsS3 {

    @Setter
    String bucketName;

    @Setter
    Regions region;

  }

  public static class Twilio {

    @Setter
    String accountSid;

    @Setter
    String authToken;

    @Setter
    String faxFrom;

  }

}
