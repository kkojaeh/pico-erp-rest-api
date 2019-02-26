package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import com.github.reinert.jjschema.SchemaIgnore;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.val;
import pico.erp.process.info.ProcessInfo;

@Data
@Attributes(title = "인쇄", description = "인쇄 공정에 필요한 정보")
public class PrintingProcessInfo implements ProcessInfo {

  @Attributes(title = "재질")
  private String material;

  @Attributes(title = "가로(mm)", description = "[0 ≦ n ≦ 1000]", maximum = 1000, maxLength = 4, required = true, format = "number")
  private Integer width = 0;

  @Attributes(title = "세로(mm)", description = "[0 ≦ n ≦ 1300]", maximum = 1300, maxLength = 4, required = true, format = "number")
  private Integer height = 0;

  @Attributes(title = "비고", format = "textarea")
  private String remark;

  @Override
  @SchemaIgnore
  public Map<String, String> getDisplayProperties() {
    val map = new HashMap<String, String>();
    map.put("가로(mm)", width + "");
    map.put("세로(mm)", height + "");
    map.put("재질", material);
    map.put("비고", remark);
    return map;
  }
}
