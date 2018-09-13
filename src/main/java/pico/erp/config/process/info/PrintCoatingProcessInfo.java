package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;
import pico.erp.process.info.ProcessInfo;

@Data
@Attributes(title = "인쇄-코팅", description = "인쇄-코팅 공정에 필요한 정보")
public class PrintCoatingProcessInfo implements ProcessInfo {

  @Attributes(title = "비고")
  private String remark;

}
