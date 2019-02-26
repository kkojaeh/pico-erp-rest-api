package pico.erp.restapi.config;

import java.math.BigDecimal;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemUnitCostEstimator;
import pico.erp.process.ProcessService;
import pico.erp.shared.Public;

@Configuration
public class OutsourcingOrderConfiguration {

  @Public
  @Component
  public static class DefaultOutsourcingOrderItemUnitCostEstimator implements
    OutsourcingOrderItemUnitCostEstimator {

    @Lazy
    @Autowired
    ProcessService processService;

    @Override
    public BigDecimal estimate(OutsourcingOrderItemContext context) {
      if (context.getProcessId() != null) {
        val process = processService.get(context.getProcessId());
        return process.getEstimatedCost().getTotal();
      }
      return null;
    }
  }

}
