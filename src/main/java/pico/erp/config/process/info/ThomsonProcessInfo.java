package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;
import pico.erp.process.data.ProcessInfo;

@Data
@Attributes(title = "톰슨", description = "톰슨 공정에 필요한 정보")
public class ThomsonProcessInfo implements ProcessInfo {

  @Attributes(title = "비고")
  private String remark;

}
