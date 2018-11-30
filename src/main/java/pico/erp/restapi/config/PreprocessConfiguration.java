package pico.erp.restapi.config;

import java.math.BigDecimal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.process.info.type.ProcessInfoTypeId;
import pico.erp.process.preprocess.type.PreprocessType;
import pico.erp.process.preprocess.type.PreprocessType.PreprocessTypeImpl;
import pico.erp.process.preprocess.type.PreprocessTypeId;
import pico.erp.shared.Public;

@Configuration
public class PreprocessConfiguration {

  @Public
  @Bean
  public PreprocessType metallicPatternPreprocessType() {
    return PreprocessTypeImpl.builder()
      .id(PreprocessTypeId.from("MP"))
      .name("금형")
      .baseCost(new BigDecimal(100000))
      .infoTypeId(ProcessInfoTypeId.from("designing"))
      .build();
  }

  @Public
  @Bean
  public PreprocessType printDesignPreprocessType() {
    return PreprocessTypeImpl.builder()
      .id(PreprocessTypeId.from("PD"))
      .name("인쇄디자인")
      .baseCost(new BigDecimal(100000))
      .infoTypeId(ProcessInfoTypeId.from("designing"))
      .build();
  }

  @Public
  @Bean
  public PreprocessType woodenPatternPreprocessType() {
    return PreprocessTypeImpl.builder()
      .id(PreprocessTypeId.from("WP"))
      .name("목형")
      .baseCost(new BigDecimal(100000))
      .infoTypeId(ProcessInfoTypeId.from("designing"))
      .build();
  }

}
