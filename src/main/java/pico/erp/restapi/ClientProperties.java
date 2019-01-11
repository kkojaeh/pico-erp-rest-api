package pico.erp.restapi;

import java.net.URI;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("client")
public class ClientProperties {

  URI locationOrigin;

}
