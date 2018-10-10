package pico.erp.restapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.config.process.info.BondingProcessInfo;
import pico.erp.config.process.info.CoatingProcessInfo;
import pico.erp.config.process.info.DesigningProcessInfo;
import pico.erp.config.process.info.EmbossingProcessInfo;
import pico.erp.config.process.info.FoilingProcessInfo;
import pico.erp.config.process.info.OutputProcessInfo;
import pico.erp.config.process.info.PackagingProcessInfo;
import pico.erp.config.process.info.PressMoldingProcessInfo;
import pico.erp.config.process.info.PrintCoatingProcessInfo;
import pico.erp.config.process.info.PrintingProcessInfo;
import pico.erp.config.process.info.ThomsonProcessInfo;
import pico.erp.process.info.type.ClassBasedProcessInfoType;
import pico.erp.process.info.type.ProcessInfoType;
import pico.erp.shared.Public;

@Configuration
public class ProcessConfiguration {

  @Public
  @Bean
  public ProcessInfoType coatingProcessInfo() {
    return new ClassBasedProcessInfoType(CoatingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType outputProcessInfo() {
    return new ClassBasedProcessInfoType(OutputProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType packagingProcessInfo() {
    return new ClassBasedProcessInfoType(PackagingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType pressMoldingProcessInfo() {
    return new ClassBasedProcessInfoType(PressMoldingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType designingProcessInfo() {
    return new ClassBasedProcessInfoType(DesigningProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType thomsonProcessInfo() {
    return new ClassBasedProcessInfoType(ThomsonProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType bondingProcessInfo() {
    return new ClassBasedProcessInfoType(BondingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType embossingProcessInfo() {
    return new ClassBasedProcessInfoType(EmbossingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType foilingProcessInfo() {
    return new ClassBasedProcessInfoType(FoilingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType printProcessInfo() {
    return new ClassBasedProcessInfoType(PrintingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType printCoatingProcessInfo() {
    return new ClassBasedProcessInfoType(PrintCoatingProcessInfo.class);
  }


}
