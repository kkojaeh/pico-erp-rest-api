package pico.erp.restapi.config;

import java.math.BigDecimal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.process.preparation.type.ProcessPreparationType;
import pico.erp.process.preparation.type.ProcessPreparationType.ProcessPreparationTypeImpl;
import pico.erp.process.preparation.type.ProcessPreparationTypeId;
import pico.erp.shared.Public;

@Configuration
public class ProcessPreparationConfiguration {

  @Public
  @Bean
  public ProcessPreparationType metallicPatternProcessPreparationType() {
    return ProcessPreparationTypeImpl.builder()
      .id(ProcessPreparationTypeId.from("MP"))
      .name("금형")
      .baseCost(new BigDecimal(100000))
      .build();
  }

  @Public
  @Bean
  public ProcessPreparationType printDesignProcessPreparationType() {
    return ProcessPreparationTypeImpl.builder()
      .id(ProcessPreparationTypeId.from("PD"))
      .name("인쇄디자인")
      .baseCost(new BigDecimal(100000))
      .build();
  }

  @Public
  @Bean
  public ProcessPreparationType woodenPatternProcessPreparationType() {
    return ProcessPreparationTypeImpl.builder()
      .id(ProcessPreparationTypeId.from("WP"))
      .name("목형")
      .baseCost(new BigDecimal(100000))
      .build();
  }

}
