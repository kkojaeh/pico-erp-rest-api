package pico.erp.config.item.spec;

import com.github.reinert.jjschema.Attributes;
import com.github.reinert.jjschema.SchemaIgnore;
import java.math.BigDecimal;
import lombok.Data;
import pico.erp.item.ItemInfo;
import pico.erp.item.spec.variables.ItemSpecVariables;
import pico.erp.shared.data.UnitKind;

@Data
@Attributes(title = "골판지 스펙", description = "골판지 정보")
public class CorrugatedFiberBoardItemSpecVariables implements ItemSpecVariables {

  @Attributes(title = "가로(mm)", description = "[545 ≦ n ≦ 1500]", minimum = 100, maximum = 1500, maxLength = 4, required = true, format = "number")
  private Integer width = 100;

  @Attributes(title = "세로(mm)", description = "[364 ≦ n ≦ 720]", minimum = 100, maximum = 1500, maxLength = 4, required = true, format = "number")
  private Integer height = 100;

  @SchemaIgnore
  private UnitKind unit = UnitKind.SHEET;

  @SchemaIgnore
  private UnitKind purchaseUnit = UnitKind.SHEET;


  @Override
  public BigDecimal calculatePurchaseQuantity(BigDecimal quantity) {
    return quantity;
  }

  @Override
  public BigDecimal calculatePurchaseUnitCost(ItemInfo item) {
    return calculateUnitCost(item);
  }

  @Override
  public BigDecimal calculateUnitCost(ItemInfo item) {
    return item.getBaseUnitCost()
      .multiply(new BigDecimal(width).divide(new BigDecimal(1000), 4, BigDecimal.ROUND_HALF_UP))
      .multiply(new BigDecimal(height).divide(new BigDecimal(1000), 4, BigDecimal.ROUND_HALF_UP));
  }

  @Override
  public String getSummary() {
    return String
      .format("%d*%d", Math.max(width, height), Math.min(width, height));
  }

  @Override
  public boolean isValid() {
    return width != null && height != null;
  }
}
