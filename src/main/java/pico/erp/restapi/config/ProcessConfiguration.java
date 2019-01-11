package pico.erp.restapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pico.erp.config.process.info.BondingProcessInfo;
import pico.erp.config.process.info.CoatingProcessInfo;
import pico.erp.config.process.info.CuttingProcessInfo;
import pico.erp.config.process.info.DesigningProcessInfo;
import pico.erp.config.process.info.EmbossingProcessInfo;
import pico.erp.config.process.info.FoilingProcessInfo;
import pico.erp.config.process.info.LaminatingProcessInfo;
import pico.erp.config.process.info.MoldingProcessInfo;
import pico.erp.config.process.info.OutputProcessInfo;
import pico.erp.config.process.info.PackagingProcessInfo;
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
  public ProcessInfoType bondingProcessInfo() {
    return new ClassBasedProcessInfoType("bonding", BondingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType coatingProcessInfo() {
    return new ClassBasedProcessInfoType("coating", CoatingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType cuttingProcessInfo() {
    return new ClassBasedProcessInfoType("cutting", CuttingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType designingProcessInfo() {
    return new ClassBasedProcessInfoType("designing", DesigningProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType embossingProcessInfo() {
    return new ClassBasedProcessInfoType("embossing", EmbossingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType foilingProcessInfo() {
    return new ClassBasedProcessInfoType("foiling", FoilingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType laminatingProcessInfo() {
    return new ClassBasedProcessInfoType("laminating", LaminatingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType moldingProcessInfo() {
    return new ClassBasedProcessInfoType("molding", MoldingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType outputProcessInfo() {
    return new ClassBasedProcessInfoType("output", OutputProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType packagingProcessInfo() {
    return new ClassBasedProcessInfoType("packaging", PackagingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType printCoatingProcessInfo() {
    return new ClassBasedProcessInfoType("print-coating", PrintCoatingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType printProcessInfo() {
    return new ClassBasedProcessInfoType("printing", PrintingProcessInfo.class);
  }

  @Public
  @Bean
  public ProcessInfoType thomsonProcessInfo() {
    return new ClassBasedProcessInfoType("thomson", ThomsonProcessInfo.class);
  }


}
