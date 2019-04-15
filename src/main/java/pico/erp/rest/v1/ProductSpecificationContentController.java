package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import pico.erp.product.specification.content.ProductSpecificationContentData;
import pico.erp.product.specification.content.ProductSpecificationContentId;
import pico.erp.product.specification.content.ProductSpecificationContentRequests;
import pico.erp.product.specification.content.ProductSpecificationContentService;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("product-specification-content-controller-v1")
@RequestMapping(value = "/product-specification", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProductSpecificationContentController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @ComponentAutowired
  private ProductSpecificationContentService productSpecificationContentService;

  /*@ComponentAutowired
  private ProductSpecificationContentQuery productSpecificationContentQuery;*/

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 사양서 조회")
  @PreAuthorize("hasAnyRole('PRODUCT_SPECIFICATION_WRITER', 'PRODUCT_SPECIFICATION_MANAGER', 'PRODUCT_SPECIFICATION_ACCESSOR')")
  @GetMapping(value = "/contents/{id}", consumes = MediaType.ALL_VALUE)
  public ProductSpecificationContentData get(
    @PathVariable("id") ProductSpecificationContentId id) {
    return productSpecificationContentService.get(id);
  }

  /*@ApiOperation(value = "품목 사양서 검색")
  @PreAuthorize("hasAnyRole('PRODUCT_SPECIFICATION_WRITER', 'PRODUCT_SPECIFICATION_MANAGER', 'PRODUCT_SPECIFICATION_ACCESSOR')")
  @GetMapping(value = "/specifications/{specificationId}/contents", consumes = MediaType.ALL_VALUE)
  public Page<ProductSpecificationContentView> retrieve(
    @PathVariable("specificationId") ProductSpecificationId specificationId,
    ProductSpecificationContentView.Filter filter,
    Pageable pageable, @AuthenticationPrincipal AuthorizedUser userDetails) {
    filter.setSpecificationId(specificationId);
    return productSpecificationContentQuery.retrieve(filter, pageable);
  }*/

  @ApiOperation(value = "품목 사양서 내용 수정")
  @PutMapping("/contents/{id}")
  @PreAuthorize("hasAnyRole('PRODUCT_SPECIFICATION_WRITER', 'PRODUCT_SPECIFICATION_MANAGER')")
  public void update(
    @PathVariable("id") ProductSpecificationContentId id,
    @RequestBody ProductSpecificationContentRequests.UpdateRequest request) {
    request.setId(id);
    productSpecificationContentService.update(request);
  }

}
