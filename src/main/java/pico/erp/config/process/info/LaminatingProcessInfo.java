package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;
import pico.erp.process.info.ProcessInfo;

@Data
@Attributes(title = "합지", description = "합지 공정에 필요한 정보")
public class LaminatingProcessInfo implements ProcessInfo {

  @Attributes(title = "재질")
  private String material;

  @Attributes(title = "가로(mm)", description = "[0 ≦ n ≦ 1000]", maximum = 1000, maxLength = 4, required = true, format = "number")
  private Integer width = 0;

  @Attributes(title = "세로(mm)", description = "[0 ≦ n ≦ 1300]", maximum = 1300, maxLength = 4, required = true, format = "number")
  private Integer height = 0;

  @Attributes(title = "비고", format = "textarea")
  private String remark;

}
