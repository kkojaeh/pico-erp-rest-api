package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;
import pico.erp.process.info.ProcessInfo;

@Data
@Attributes(title = "형압", description = "형압 공정에 필요한 정보")
public class EmbossingProcessInfo implements ProcessInfo {

  @Attributes(title = "비고")
  private String remark;

}
