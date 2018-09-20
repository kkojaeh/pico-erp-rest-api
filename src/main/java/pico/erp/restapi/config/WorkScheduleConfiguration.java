package pico.erp.restapi.config;

import java.time.LocalTime;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.shared.Public;
import pico.erp.work.schedule.category.data.WorkScheduleCategory;
import pico.erp.work.schedule.category.data.WorkScheduleCategory.WorkScheduleCategoryImpl;
import pico.erp.work.schedule.category.data.WorkScheduleCategoryId;
import pico.erp.work.schedule.data.WorkScheduleTimeData;

@Configuration
public class WorkScheduleConfiguration {

  @Bean
  @Public
  public WorkScheduleCategory printingWorkScheduleCategory() {
    return new WorkScheduleCategoryImpl(
      WorkScheduleCategoryId.from("printing"),
      "인쇄 작업일",
      Arrays.asList(
        new WorkScheduleTimeData(LocalTime.parse("09:00"), LocalTime.parse("12:00")),
        new WorkScheduleTimeData(LocalTime.parse("13:00"), LocalTime.parse("18:00"))
      )
    );
  }

}
