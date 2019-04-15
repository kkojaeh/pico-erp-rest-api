package pico.erp.rest.config;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import kkojaeh.spring.boot.component.ComponentBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.work.schedule.category.WorkScheduleCategory;
import pico.erp.work.schedule.category.WorkScheduleCategory.WorkScheduleCategoryImpl;
import pico.erp.work.schedule.category.WorkScheduleCategoryId;
import pico.erp.work.schedule.time.WorkScheduleTimeData;

@Configuration
public class WorkScheduleConfiguration {

  @Bean
  @ComponentBean(host = false)
  public WorkScheduleCategory printingWorkScheduleCategory() {
    return new WorkScheduleCategoryImpl(
      WorkScheduleCategoryId.from("printing"),
      "인쇄 작업일",
      ZoneId.of("Asia/Seoul"),
      Arrays.asList(
        new WorkScheduleTimeData(LocalTime.parse("09:00"), LocalTime.parse("12:00")),
        new WorkScheduleTimeData(LocalTime.parse("13:00"), LocalTime.parse("18:00"))
      )
    );
  }

}
