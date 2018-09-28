package pico.erp.restapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.facility.category.data.FacilityCategory;
import pico.erp.facility.category.data.FacilityCategory.FacilityCategoryImpl;
import pico.erp.facility.category.data.FacilityCategoryId;
import pico.erp.shared.Public;

@Configuration
public class FacilityConfiguration {

  @Bean
  @Public
  public FacilityCategory packagingFacilityCategory() {
    return FacilityCategoryImpl.builder()
      .id(FacilityCategoryId.from("packaging"))
      .name("포장")
      .build();
  }

  @Bean
  @Public
  public FacilityCategory pressMoldingFacilityCategory() {
    return FacilityCategoryImpl.builder()
      .id(FacilityCategoryId.from("press-molding"))
      .name("압착 성형")
      .build();
  }

}
