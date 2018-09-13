package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;
import pico.erp.process.info.ProcessInfo;

@Data
@Attributes(title = "압착 성형", description = "압착 성형 공정에 필요한 정보")
public class PressMoldingProcessInfo implements ProcessInfo {

  @Attributes(title = "비고")
  private String remark;

}
