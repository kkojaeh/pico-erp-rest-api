package pico.erp.rest.config;

import kkojaeh.spring.boot.component.ComponentBean;
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

@Configuration
public class ProcessConfiguration {

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType bondingProcessInfo() {
    return new ClassBasedProcessInfoType("bonding", BondingProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType coatingProcessInfo() {
    return new ClassBasedProcessInfoType("coating", CoatingProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType cuttingProcessInfo() {
    return new ClassBasedProcessInfoType("cutting", CuttingProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType designingProcessInfo() {
    return new ClassBasedProcessInfoType("designing", DesigningProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType embossingProcessInfo() {
    return new ClassBasedProcessInfoType("embossing", EmbossingProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType foilingProcessInfo() {
    return new ClassBasedProcessInfoType("foiling", FoilingProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType laminatingProcessInfo() {
    return new ClassBasedProcessInfoType("laminating", LaminatingProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType moldingProcessInfo() {
    return new ClassBasedProcessInfoType("molding", MoldingProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType outputProcessInfo() {
    return new ClassBasedProcessInfoType("output", OutputProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType packagingProcessInfo() {
    return new ClassBasedProcessInfoType("packaging", PackagingProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType printCoatingProcessInfo() {
    return new ClassBasedProcessInfoType("print-coating", PrintCoatingProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType printProcessInfo() {
    return new ClassBasedProcessInfoType("printing", PrintingProcessInfo.class);
  }

  @ComponentBean(host = false)
  @Bean
  public ProcessInfoType thomsonProcessInfo() {
    return new ClassBasedProcessInfoType("thomson", ThomsonProcessInfo.class);
  }


}
