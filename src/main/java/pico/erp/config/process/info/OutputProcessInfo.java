package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;
import pico.erp.process.info.ProcessInfo;

@Data
@Attributes(title = "출력", description = "출력 공정에 필요한 정보")
public class OutputProcessInfo implements ProcessInfo {

  @Attributes(title = "전면 도수")
  private String frontColors;

  @Attributes(title = "후면 도수")
  private String backColors;

  @Attributes(title = "비고", format = "textarea")
  private String remark;

}
