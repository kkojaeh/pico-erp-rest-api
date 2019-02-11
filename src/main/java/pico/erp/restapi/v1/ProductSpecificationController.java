package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.item.ItemId;
import pico.erp.product.specification.ProductSpecificationData;
import pico.erp.product.specification.ProductSpecificationId;
import pico.erp.product.specification.ProductSpecificationRequests;
import pico.erp.product.specification.ProductSpecificationService;
import pico.erp.product.specification.ProductSpecificationStatusKind;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.UserId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("product-specification-controller-v1")
@RequestMapping(value = "/product-specification", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProductSpecificationController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private ProductSpecificationService productSpecificationService;

  @ApiOperation(value = "품목 사양서 제출")
  @PutMapping("/specifications/{id}/commit")
  @PreAuthorize("hasAnyRole('PRODUCT_SPECIFICATION_WRITER', 'PRODUCT_SPECIFICATION_MANAGER')")
  public void commit(@PathVariable("id") ProductSpecificationId id,
    @RequestBody ProductSpecificationRequests.CommitRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setCommitterId(UserId.from(userDetails.getUsername()));
    productSpecificationService.commit(request);
  }

  @ApiOperation(value = "품목 사양서 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/specifications")
  @PreAuthorize("hasAnyRole('PRODUCT_SPECIFICATION_WRITER', 'PRODUCT_SPECIFICATION_MANAGER')")
  public ProductSpecificationData draft(
    @RequestBody ProductSpecificationRequests.DraftRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    return productSpecificationService.draft(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 사양서 조회")
  @PreAuthorize("hasAnyRole('PRODUCT_SPECIFICATION_WRITER', 'PRODUCT_SPECIFICATION_MANAGER', 'PRODUCT_SPECIFICATION_ACCESSOR')")
  @GetMapping(value = "/specifications/{id}", consumes = MediaType.ALL_VALUE)
  public ProductSpecificationData get(@PathVariable("id") ProductSpecificationId id) {
    return productSpecificationService.get(id);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 사양서 조회")
  @PreAuthorize("hasAnyRole('PRODUCT_SPECIFICATION_WRITER', 'PRODUCT_SPECIFICATION_MANAGER', 'PRODUCT_SPECIFICATION_ACCESSOR')")
  @GetMapping(value = "/items/{itemId}", consumes = MediaType.ALL_VALUE)
  public ProductSpecificationData get(@PathVariable("itemId") ItemId itemId) {
    return productSpecificationService.get(itemId);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "품목 사양서 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(ProductSpecificationStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

}
