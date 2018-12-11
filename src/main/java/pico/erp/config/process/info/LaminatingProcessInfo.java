package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;
import pico.erp.process.info.ProcessInfo;

@Data
@Attributes(title = "합지", description = "합지 공정에 필요한 정보")
public class LaminatingProcessInfo implements ProcessInfo {

  @Attributes(title = "비고")
  private String remark;

}
