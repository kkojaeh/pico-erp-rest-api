package pico.erp.config.process.info;

import com.github.reinert.jjschema.Attributes;
import lombok.Data;
import pico.erp.process.data.ProcessInfo;

@Data
@Attributes(title = "인쇄", description = "인쇄 공정에 필요한 정보")
public class PrintingProcessInfo implements ProcessInfo {

  @Attributes(title = "입고수량")
  private Integer putQuantity;

  @Attributes(title = "정매수량")
  private Integer materialQuantity;

  @Attributes(title = "재질")
  private String material;

  @Attributes(title = "넓이", description = "(mm)")
  private Integer width;

  @Attributes(title = "높이", description = "(mm)")
  private Integer height;

  @Attributes(title = "두께", description = "(mm)")
  private Integer thickness;

  @Attributes(title = "비고")
  private String remark;
}
