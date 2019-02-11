package pico.erp.config.item.spec;

import com.github.reinert.jjschema.Attributes;
import com.github.reinert.jjschema.SchemaIgnore;
import java.math.BigDecimal;
import lombok.Data;
import pico.erp.item.ItemInfo;
import pico.erp.item.spec.variables.ItemSpecVariables;
import pico.erp.shared.data.UnitKind;

@Data
@Attributes(title = "원지 스펙", description = "원지 정보")
public class MaterialPaperItemSpecVariables implements ItemSpecVariables {

  @Attributes(title = "평량", enums = {"180", "200", "220", "240", "260", "280", "300", "350", "400",
    "450", "500", "550"}, required = true)
  private Integer grammage = 180;

  @Attributes(title = "가로(mm)", description = "[545 ≦ n ≦ 1500]", minimum = 545, maximum = 1500, maxLength = 4, required = true, format = "number")
  private Integer width = 545;

  @Attributes(title = "세로(mm)", description = "[364 ≦ n ≦ 720]", minimum = 364, maximum = 1500, maxLength = 4, required = true, format = "number")
  private Integer height = 364;

  /*
  @Attributes(title = "여분율(‰)[1000‰ = 100%]", minimum = 0, maximum = 1000 )
  private Integer extraRate;
  */

  @Attributes(title = "절수", minimum = 1, maximum = 4, enums = {"1", "2", "3", "4"})
  private Integer incisionCount = 1;

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
    double paperConstant = 0.5;
    BigDecimal unitCost = item.getBaseUnitCost().multiply(
      new BigDecimal(grammage)
        .multiply(new BigDecimal(width).divide(new BigDecimal(1000)))
        .multiply(new BigDecimal(height).divide(new BigDecimal(1000)))
        .divide(new BigDecimal(incisionCount).multiply(new BigDecimal(paperConstant)))
    );
    return unitCost.setScale(2, BigDecimal.ROUND_HALF_UP);
  }

  @Override
  public String getSummary() {
    return String
      .format("%d (%d*%d) / %d", grammage, width, height,
        incisionCount);
  }

  @SchemaIgnore
  @Override
  public boolean isValid() {
    return grammage != null && width != null && height != null && incisionCount != null;
  }


}
