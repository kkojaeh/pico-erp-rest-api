package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;
import pico.erp.process.info.ProcessInfo;

@Data
@Attributes(title = "커팅", description = "커팅 공정에 필요한 정보")
public class CuttingProcessInfo implements ProcessInfo {

  @Attributes(title = "비고")
  private String remark;

}
