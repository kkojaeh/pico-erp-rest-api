package pico.erp.restapiserver.attachment;

import java.time.LocalTime;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.shared.Public;
import pico.erp.workday.data.WorkDayCategory;
import pico.erp.workday.data.WorkDayCategoryId;
import pico.erp.workday.data.WorkTimeData;
import pico.erp.workday.impl.WorkDayCategoryImpl;

@Configuration
public class WorkDayConfiguration {

  @Bean
  @Public
  public WorkDayCategory printingWorkDayCategory() {
    return new WorkDayCategoryImpl(
      WorkDayCategoryId.from("printing"),
      "인쇄 작업일",
      Arrays.asList(
        new WorkTimeData(LocalTime.parse("09:00"), LocalTime.parse("12:00")),
        new WorkTimeData(LocalTime.parse("13:00"), LocalTime.parse("18:00"))
      )
    );
  }

}
