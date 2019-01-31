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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import pico.erp.production.plan.ProductionPlanData;
import pico.erp.production.plan.ProductionPlanId;
import pico.erp.production.plan.ProductionPlanQuery;
import pico.erp.production.plan.ProductionPlanRequests;
import pico.erp.production.plan.ProductionPlanService;
import pico.erp.production.plan.ProductionPlanStatusKind;
import pico.erp.production.plan.ProductionPlanView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("production-plan-controller-v1")
@RequestMapping(value = "/production-plan", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProductionPlanController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private ProductionPlanService productionPlanService;

  @Lazy
  @Autowired
  private ProductionPlanQuery productionPlanQuery;

  @ApiOperation(value = "생산 계획 취소")
  @PutMapping("/plans/{id}/cancel")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  public void cancel(@PathVariable("id") ProductionPlanId id,
    @RequestBody ProductionPlanRequests.CancelRequest request) {
    request.setId(id);
    productionPlanService.cancel(request);
  }

  @ApiOperation(value = "생산 계획 완료")
  @PutMapping("/plans/{id}/complete")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  public void complete(@PathVariable("id") ProductionPlanId id,
    @RequestBody ProductionPlanRequests.CompleteRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    productionPlanService.complete(request);
  }

  @ApiOperation(value = "생산 계획 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/plans")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  public ProductionPlanData create(@RequestBody ProductionPlanRequests.CreateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    return productionPlanService.create(request);
  }

  @ApiOperation(value = "생산 계획 확정")
  @PutMapping("/plans/{id}/determine")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  public void determine(@PathVariable("id") ProductionPlanId id,
    @RequestBody ProductionPlanRequests.DetermineRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    productionPlanService.determine(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "생산 계획 조회")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  @GetMapping(value = "/plans/{id}", consumes = MediaType.ALL_VALUE)
  public ProductionPlanData get(@PathVariable("id") ProductionPlanId id) {
    return productionPlanService.get(id);
  }

  /*
  @ApiOperation(value = "생산 계획 삭제")
  @DeleteMapping("/plans/{id}")
  @PreAuthorize("hasRole('PRODUCTION_PLAN_MANAGER')")
  public void delete(@PathVariable("id") ProductionPlanId id) {
    productionRequestService.delete(new ProductionPlanRequests.DeleteRequest(id));
  }
  */

  @ApiOperation(value = "생산 계획 검색")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  @GetMapping(value = "/plans", consumes = MediaType.ALL_VALUE)
  public Page<ProductionPlanView> retrieve(ProductionPlanView.Filter filter,
    Pageable pageable, @AuthenticationPrincipal AuthorizedUser userDetails) {
    return productionPlanQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "생산 계획 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(ProductionPlanStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "생산 계획 수정")
  @PutMapping("/plans/{id}")
  @PreAuthorize("hasAnyRole('PRODUCTION_PLAN_CHARGER', 'PRODUCTION_PLAN_MANAGER')")
  public void update(@PathVariable("id") ProductionPlanId id,
    @RequestBody ProductionPlanRequests.UpdateRequest request) {
    request.setId(id);
    productionPlanService.update(request);
  }

}
