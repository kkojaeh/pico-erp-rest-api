package pico.erp.rest.config;

import java.math.BigDecimal;
import kkojaeh.spring.boot.component.ComponentAutowired;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.val;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemUnitCostEstimator;
import pico.erp.process.ProcessService;

@Configuration
public class OutsourcingOrderConfiguration {

  @ComponentBean(host = false)
  @Component
  public static class DefaultOutsourcingOrderItemUnitCostEstimator implements
    OutsourcingOrderItemUnitCostEstimator {

    @ComponentAutowired
    private ProcessService processService;

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
