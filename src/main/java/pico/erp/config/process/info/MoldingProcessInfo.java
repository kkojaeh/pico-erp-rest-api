package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;
import pico.erp.process.info.ProcessInfo;

@Data
@Attributes(title = "성형", description = "성형 공정에 필요한 정보")
public class MoldingProcessInfo implements ProcessInfo {

  @Attributes(title = "비고", format = "textarea")
  private String remark;

}
