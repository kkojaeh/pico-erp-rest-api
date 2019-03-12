package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.production.plan.detail.ProductionPlanDetailData;
import pico.erp.production.plan.detail.ProductionPlanDetailId;
import pico.erp.production.plan.detail.ProductionPlanDetailProgressTypeKind;
import pico.erp.production.plan.detail.ProductionPlanDetailRequests;
import pico.erp.production.plan.detail.ProductionPlanDetailService;
import pico.erp.production.plan.detail.ProductionPlanDetailStatusKind;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("production-plan-detail-controller-v1")
@RequestMapping(value = "/production-plan", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProductionPlanDetailController {

  @Autowired
  MessageSource messageSource;

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ProductionPlanDetailService productionPlanDetailService;

  @ApiOperation(value = "구매 요청 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/details")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  public ProductionPlanDetailData create(
    @RequestBody ProductionPlanDetailRequests.CreateRequest request) {
    return productionPlanDetailService.create(request);
  }

  @ApiOperation(value = "구매 요청 품목 삭제")
  @DeleteMapping("/details/{id}")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  public void delete(@PathVariable("id") ProductionPlanDetailId id) {
    productionPlanDetailService.delete(new ProductionPlanDetailRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "구매 요청 품목 확정")
  @PutMapping("/details/{id}/determine")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  public void determine(@PathVariable("id") ProductionPlanDetailId id,
    @RequestBody ProductionPlanDetailRequests.DetermineRequest request) {
    request.setId(id);
    productionPlanDetailService.determine(request);
  }

  @ApiOperation(value = "구매 요청 품목 조회")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  @GetMapping(value = "/plans/{planId}/details", consumes = MediaType.ALL_VALUE)
  public List<ProductionPlanDetailData> getAll(
    @PathVariable("planId") ProductionPlanId planId) {
    return productionPlanDetailService.getAll(planId);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "생산 계획 상세 진행 유형 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/detail-progress-type-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> progressTypeLabels() {

    return Stream.of(ProductionPlanDetailProgressTypeKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "생산 계획 상세 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/detail-status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(ProductionPlanDetailStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "생산 계획 조회")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  @GetMapping(value = "/details/{id}", consumes = MediaType.ALL_VALUE)
  public ProductionPlanDetailData get(@PathVariable("id") ProductionPlanDetailId id) {
    return productionPlanDetailService.get(id);
  }

  @ApiOperation(value = "구매 요청 품목 수정")
  @PutMapping("/details/{id}")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  public void update(
    @PathVariable("id") ProductionPlanDetailId id,
    @RequestBody ProductionPlanDetailRequests.UpdateRequest request) {
    request.setId(id);
    productionPlanDetailService.update(request);
  }

}
