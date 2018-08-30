package pico.erp.config.item.spec;

import com.github.reinert.jjschema.Attributes;
import com.github.reinert.jjschema.SchemaIgnore;
import lombok.Data;
import pico.erp.item.data.ItemSpecVariables;

@Data
@Attributes(title = "인쇄", description = "인쇄 공정에 필요한 정보")
public class PaperItemSpecVariables implements ItemSpecVariables {

  @Attributes(title = "평량", enums = {"180", "200", "220", "240", "260", "280", "300", "350", "400",
    "450", "500", "550"}, required = true)
  private Integer grammage = 180;

  @Attributes(title = "가로(mm) [545 ≦ n ≦ 1020]", minimum = 545, maximum = 1020, maxLength = 4, required = true, format = "number")
  private Integer width = 545;

  @Attributes(title = "세로(mm) [364 ≦ n ≦ 720]", minimum = 364, maximum = 720, maxLength = 3, required = true, format = "number")
  private Integer height = 364;

  /*
  @Attributes(title = "여분율(‰)[1000‰ = 100%]", minimum = 0, maximum = 1000 )
  private Integer extraRate;
  */

  @Attributes(title = "절수", minimum = 1, maximum = 4, enums = {"1", "2", "3", "4"})
  private Integer incisionCount = 1;

  @Override
  public String getSummary() {
    return String.format("%d (%d×%d) / %d", grammage, width, height, incisionCount);
  }

  @SchemaIgnore
  @Override
  public boolean isValid() {
    return grammage != null && width != null && height != null && incisionCount != null;
  }

}
