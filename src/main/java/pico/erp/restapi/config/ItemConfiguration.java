package pico.erp.restapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.config.item.spec.MaterialPaperItemSpecVariables;
import pico.erp.config.item.spec.PetItemSpecVariables;
import pico.erp.item.spec.type.ClassBasedItemSpecType;
import pico.erp.item.spec.type.ItemSpecType;
import pico.erp.shared.Public;

@Configuration
public class ItemConfiguration {

  @Public
  @Bean
  public ItemSpecType<MaterialPaperItemSpecVariables> materialPaperItemSpecType() {
    return new ClassBasedItemSpecType<>("material-paper", MaterialPaperItemSpecVariables.class);
  }

  @Public
  @Bean
  public ItemSpecType<PetItemSpecVariables> petItemSpecType() {
    return new ClassBasedItemSpecType<>("pet", PetItemSpecVariables.class);
  }

}
