package pico.erp.restapi.config;

import java.math.BigDecimal;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pico.erp.item.ItemService;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.purchase.order.item.PurchaseOrderItemUnitCostEstimator;
import pico.erp.shared.Public;

@Configuration
public class PurchaseOrderConfiguration {

  @Public
  @Component
  public static class DefaultPurchaseOrderItemUnitCostEstimator implements
    PurchaseOrderItemUnitCostEstimator {

    @Lazy
    @Autowired
    ItemService itemService;

    @Lazy
    @Autowired
    ItemSpecService itemSpecService;

    @Override
    public BigDecimal estimate(PurchaseOrderItemContext context) {
      if (context.getItemSpecId() != null) {
        val itemSpec = itemSpecService.get(context.getItemSpecId());
        return itemSpec.getPurchaseUnitCost();
      }
      val item = itemService.get(context.getItemId());
      if (BigDecimal.ZERO.compareTo(item.getBaseUnitCost()) == 0) {
        return null;
      } else {
        return item.getBaseUnitCost();
      }
    }

  }

}
