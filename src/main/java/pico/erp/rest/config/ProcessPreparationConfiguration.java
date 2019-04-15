package pico.erp.rest.config;

import java.math.BigDecimal;
import kkojaeh.spring.boot.component.ComponentBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.process.preparation.type.ProcessPreparationType;
import pico.erp.process.preparation.type.ProcessPreparationType.ProcessPreparationTypeImpl;
import pico.erp.process.preparation.type.ProcessPreparationTypeId;

@Configuration
public class ProcessPreparationConfiguration {

  @ComponentBean(host = false)
  @Bean
  public ProcessPreparationType metallicPatternProcessPreparationType() {
    return ProcessPreparationTypeImpl.builder()
      .id(ProcessPreparationTypeId.from("MP"))
      .name("금형")
      .baseCost(new BigDecimal(100000))
      .build();
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessPreparationType printDesignProcessPreparationType() {
    return ProcessPreparationTypeImpl.builder()
      .id(ProcessPreparationTypeId.from("PD"))
      .name("인쇄디자인")
      .baseCost(new BigDecimal(100000))
      .build();
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessPreparationType woodenPatternProcessPreparationType() {
    return ProcessPreparationTypeImpl.builder()
      .id(ProcessPreparationTypeId.from("WP"))
      .name("목형")
      .baseCost(new BigDecimal(100000))
      .build();
  }

}
