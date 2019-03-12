package pico.erp.restapi.config;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.io.File;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import pico.erp.document.context.DocumentContext;
import pico.erp.document.context.DocumentContextFactory;
import pico.erp.document.context.DocumentContextFactoryImpl;
import pico.erp.document.maker.DocumentMakerDefinition;
import pico.erp.document.maker.PdfmakeDocumentMakerDefinition;
import pico.erp.document.storage.AwsS3DocumentStorageStrategy;
import pico.erp.document.storage.DocumentStorageStrategy;
import pico.erp.document.storage.FileSystemDocumentStorageStrategy;
import pico.erp.document.subject.DocumentSubjectId;
import pico.erp.document.subject.DocumentSubjectRequests;
import pico.erp.document.subject.DocumentSubjectService;
import pico.erp.shared.ApplicationInitializer;
import pico.erp.shared.Public;
import pico.erp.shared.data.ContentInputStream;

@Configuration
@ConfigurationProperties(prefix = "document")
public class DocumentConfiguration {

  @Getter
  AwsS3 awsS3 = new AwsS3();

  @Getter
  Logo logo = new Logo();

  @Public
  @Bean
  @Profile({"production", "development"})
  public DocumentStorageStrategy awsS3DocumentStorageStrategy() {
    val amazonS3 = AmazonS3ClientBuilder.standard()
      .withRegion(awsS3.region)
      .withCredentials(new EnvironmentVariableCredentialsProvider()).build();
    val config = AwsS3DocumentStorageStrategy.Config.builder()
      .amazonS3(amazonS3)
      .amazonS3BucketName(awsS3.bucketName)
      .build();
    return new AwsS3DocumentStorageStrategy(config);
  }

  @Public
  @Bean
  public DocumentContextFactory documentContextFactory() {

    return new DocumentContextFactoryImpl() {

      @SneakyThrows
      @Override
      public DocumentContext factory() {
        val result = super.factory();
        val logoContent = ContentInputStream.builder()
          .contentType(logo.contentType)
          .contentLength(logo.resource.contentLength())
          .inputStream(logo.resource.getInputStream())
          .build();

        result.getData().put("logo", result.getContentEncoder().apply(logoContent));
        return result;
      }
    };
  }

  @SneakyThrows
  @Public
  @Bean
  public DocumentMakerDefinition documentMakerDefinition(
    @Value("${document.pdfmake.workspace}") File workspace) {
    val config = PdfmakeDocumentMakerDefinition.Config.builder()
      .workspace(workspace)
      .build();
    return new PdfmakeDocumentMakerDefinition(config);
  }

  @Public
  @Bean
  @ConditionalOnMissingBean(DocumentStorageStrategy.class)
  public DocumentStorageStrategy fileSystemDocumentStorageStrategy(
    @Value("${document.storage.root-dir}") File rootDirectory) {
    val config = FileSystemDocumentStorageStrategy.Config.builder()
      .rootDirectory(rootDirectory)
      .build();
    return new FileSystemDocumentStorageStrategy(config);
  }

  public static class AwsS3 {

    @Setter
    String bucketName;

    @Setter
    Regions region;

  }

  public static class Logo {

    @Setter
    Resource resource;

    @Setter
    String contentType;

  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Configuration
  @Profile({"default", "test"})
  public static class DocumentTemplateInitializer implements ApplicationInitializer {

    @Value("classpath:document/outsourcing-order-draft-pdf.mustache")
    Resource outsourcingOrderDraftPdf;

    @Value("classpath:document/product-specification-pdf.mustache")
    Resource productSpecificationPdf;

    @Value("classpath:document/purchase-order-draft-pdf.mustache")
    Resource purchaseOrderDraftPdf;

    @Lazy
    @Autowired
    private DocumentSubjectService documentSubjectService;

    @SneakyThrows
    @Override
    public void initialize() {

      documentSubjectService.update(
        DocumentSubjectRequests.UpdateRequest.builder()
          .id(DocumentSubjectId.from("outsourcing-order-draft"))
          .name("[outsourcing-order] 외주 발주서")
          .template(IOUtils.toString(outsourcingOrderDraftPdf.getInputStream(), "utf-8"))
          .build()
      );

      documentSubjectService.update(
        DocumentSubjectRequests.UpdateRequest.builder()
          .id(DocumentSubjectId.from("product-specification"))
          .name("[product-specification] 제품 사양서")
          .template(IOUtils.toString(productSpecificationPdf.getInputStream(), "utf-8"))
          .build()
      );

      documentSubjectService.update(
        DocumentSubjectRequests.UpdateRequest.builder()
          .id(DocumentSubjectId.from("purchase-order-draft"))
          .name("[purchase-order] 발주서")
          .template(IOUtils.toString(purchaseOrderDraftPdf.getInputStream(), "utf-8"))
          .build()
      );


    }
  }

}
