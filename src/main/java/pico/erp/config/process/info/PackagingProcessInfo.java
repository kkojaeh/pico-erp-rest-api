package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;
import pico.erp.process.data.ProcessInfo;

@Data
@Attributes(title = "포장", description = "포장 공정에 필요한 정보")
public class PackagingProcessInfo implements ProcessInfo {

  @Attributes(title = "비고")
  private String remark;

}
