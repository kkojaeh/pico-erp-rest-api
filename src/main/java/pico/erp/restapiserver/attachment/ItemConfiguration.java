package pico.erp.restapiserver.attachment;

import java.math.BigDecimal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.config.item.spec.PaperItemSpecVariables;
import pico.erp.item.data.ItemSpecType;
import pico.erp.item.impl.ClassBasedItemSpecType;
import pico.erp.shared.Public;

@Configuration
public class ItemConfiguration {

  @Public
  @Bean
  public ItemSpecType<PaperItemSpecVariables> paperItemSpecType() {
    return new ClassBasedItemSpecType<>(PaperItemSpecVariables.class,
      (item, variables) -> {
        double paperConstant = 0.5;
        BigDecimal unitCost = item.getBaseUnitCost().multiply(
          new BigDecimal(variables.getGrammage())
            .multiply(new BigDecimal(variables.getWidth()).divide(new BigDecimal(1000)))
            .multiply(new BigDecimal(variables.getHeight()).divide(new BigDecimal(1000)))
            .divide(new BigDecimal(variables
              .getIncisionCount()).multiply(new BigDecimal(paperConstant)))
        );

        return unitCost.setScale(2, BigDecimal.ROUND_HALF_UP);
      });
  }

}
