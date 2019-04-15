package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.product.specification.content.ProductSpecificationContentId;
import pico.erp.product.specification.content.process.ProductSpecificationContentProcessData;
import pico.erp.product.specification.content.process.ProductSpecificationContentProcessId;
import pico.erp.product.specification.content.process.ProductSpecificationContentProcessRequests;
import pico.erp.product.specification.content.process.ProductSpecificationContentProcessService;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("product-specification-content-process-controller-v1")
@RequestMapping(value = "/product-specification", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProductSpecificationContentProcessController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @ComponentAutowired
  private ProductSpecificationContentProcessService productSpecificationContentProcessService;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 사양서 내용 공정 조회")
  @PreAuthorize("hasAnyRole('PRODUCT_SPECIFICATION_WRITER', 'PRODUCT_SPECIFICATION_MANAGER', 'PRODUCT_SPECIFICATION_ACCESSOR')")
  @GetMapping(value = "/processes/{id}", consumes = MediaType.ALL_VALUE)
  public ProductSpecificationContentProcessData get(
    @PathVariable("id") ProductSpecificationContentProcessId id) {
    return productSpecificationContentProcessService.get(id);
  }

  @ApiOperation(value = "품목 사양서 공정 검색")
  @PreAuthorize("hasAnyRole('PRODUCT_SPECIFICATION_WRITER', 'PRODUCT_SPECIFICATION_MANAGER', 'PRODUCT_SPECIFICATION_ACCESSOR')")
  @GetMapping(value = "/contents/{contentId}/processes", consumes = MediaType.ALL_VALUE)
  public List<ProductSpecificationContentProcessData> retrieve(
    @PathVariable("contentId") ProductSpecificationContentId contentId) {
    return productSpecificationContentProcessService.getAll(contentId);
  }

  @ApiOperation(value = "품목 사양서 내용 공정 수정")
  @PutMapping("/processes/{id}")
  @PreAuthorize("hasAnyRole('PRODUCT_SPECIFICATION_WRITER', 'PRODUCT_SPECIFICATION_MANAGER')")
  public void update(
    @PathVariable("id") ProductSpecificationContentProcessId id,
    @RequestBody ProductSpecificationContentProcessRequests.UpdateRequest request) {
    request.setId(id);
    productSpecificationContentProcessService.update(request);
  }

}
