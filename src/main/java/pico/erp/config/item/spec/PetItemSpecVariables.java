package pico.erp.config.item.spec;

import com.github.reinert.jjschema.Attributes;
import com.github.reinert.jjschema.SchemaIgnore;
import java.math.BigDecimal;
import lombok.Data;
import lombok.val;
import pico.erp.item.ItemInfo;
import pico.erp.item.spec.variables.ItemSpecVariables;
import pico.erp.shared.data.UnitKind;

@Data
@Attributes(title = "PET 스펙", description = "PET 정보")
public class PetItemSpecVariables implements ItemSpecVariables {

  @Attributes(title = "두께(㎛)", description = "[1000㎛ = 1mm | 200 ≦ n ≦ 2000]", minimum = 200, maximum = 2000, maxLength = 4, required = true, format = "number")
  private Integer thickness = 1000;

  @Attributes(title = "폭(mm)", description = "[0 ≦ n ≦ 2000]", minimum = 0, maximum = 2000, maxLength = 4, required = true, format = "number")
  private Integer width = 1000;

  @Attributes(title = "색상", enums = {"투명", "금색(펄)", "진금색", "연금색"}, required = true)
  private String color = "투명";

  @Attributes(title = "방향", enums = {"양면", "단면"}, required = true)
  private String side = "";

  @Attributes(title = "색상 단가", required = true, format = "number")
  private Integer colorCost = 0;

  @SchemaIgnore
  private UnitKind unit = UnitKind.M;

  @SchemaIgnore
  private UnitKind purchaseUnit = UnitKind.KG;

  @Override
  public BigDecimal calculatePurchaseQuantity(BigDecimal quantity) {
    val weightConstant = new BigDecimal(1.4);
    val lengthCentimeter = quantity.multiply(new BigDecimal(100)); // 1m
    val thicknessCentimeter = new BigDecimal(thickness)
      .divide(new BigDecimal(10000), 4, BigDecimal.ROUND_HALF_UP);
    val widthCentimeter = new BigDecimal(width).divide(BigDecimal.TEN, 2, BigDecimal.ROUND_HALF_UP);
    return lengthCentimeter
      .multiply(thicknessCentimeter)
      .multiply(widthCentimeter)
      .multiply(weightConstant)
      .divide(new BigDecimal(1000), 4, BigDecimal.ROUND_HALF_UP)
      .setScale(0, BigDecimal.ROUND_HALF_UP);
  }

  @Override
  public BigDecimal calculatePurchaseUnitCost(ItemInfo item) {
    return item.getBaseUnitCost().add(new BigDecimal(colorCost));
  }

  @Override
  public BigDecimal calculateUnitCost(ItemInfo item) {
    val weightConstant = new BigDecimal(1.4);
    val lengthCentimeter = new BigDecimal(100); // 1m
    val thicknessCentimeter = new BigDecimal(thickness)
      .divide(new BigDecimal(10000), 5, BigDecimal.ROUND_HALF_UP);
    val widthCentimeter = new BigDecimal(width).divide(BigDecimal.TEN, 2, BigDecimal.ROUND_HALF_UP);
    val kilogramPerMeter = lengthCentimeter
      .multiply(thicknessCentimeter)
      .multiply(widthCentimeter)
      .multiply(weightConstant)
      .divide(new BigDecimal(1000), 4, BigDecimal.ROUND_HALF_UP);

    val costPerKilogram = item.getBaseUnitCost().add(new BigDecimal(colorCost));
    return kilogramPerMeter.multiply(costPerKilogram)
      .add(new BigDecimal(colorCost))
      .setScale(2, BigDecimal.ROUND_HALF_UP);
  }

  @Override
  public String getSummary() {
    return String.format("%s %s*%04d %s",
      side,
      new BigDecimal(thickness).divide(new BigDecimal(1000), 3, BigDecimal.ROUND_HALF_UP)
        .toString(),
      width,
      color);
  }

  @Override
  public boolean isValid() {
    return thickness != null && width != null;
  }

}
