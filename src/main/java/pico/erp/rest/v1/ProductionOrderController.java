package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.stream.Stream;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
import pico.erp.production.order.ProductionOrderApi.Roles;
import pico.erp.production.order.ProductionOrderAwaitAcceptView;
import pico.erp.production.order.ProductionOrderAwaitExecutionView;
import pico.erp.production.order.ProductionOrderData;
import pico.erp.production.order.ProductionOrderId;
import pico.erp.production.order.ProductionOrderQuery;
import pico.erp.production.order.ProductionOrderRequests;
import pico.erp.production.order.ProductionOrderService;
import pico.erp.production.order.ProductionOrderStatusKind;
import pico.erp.production.order.ProductionOrderView;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.UserId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("production-order-controller-v1")
@RequestMapping(value = "/production-order", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProductionOrderController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @ComponentAutowired
  private ProductionOrderService productionOrderService;

  @ComponentAutowired
  private ProductionOrderQuery productionOrderQuery;

  @ApiOperation(value = "구매 요청 접수")
  @PutMapping("/orders/{id}/accept")
  @PreAuthorize("hasAnyRole('PRODUCTION_ORDER_ACCEPTER', 'PRODUCTION_ORDER_MANAGER')")
  public void accept(@PathVariable("id") ProductionOrderId id,
    @RequestBody ProductionOrderRequests.AcceptRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setAccepterId(UserId.from(userDetails.getUsername()));
    productionOrderService.accept(request);

    productionOrderService.plan(
      ProductionOrderRequests.PlanRequest.builder()
        .id(id)
        .build()
    );
  }

  @ApiOperation(value = "접수 대기 목록")
  @PreAuthorize("hasAnyRole('PRODUCTION_ORDER_ACCEPTER', 'PRODUCTION_ORDER_MANAGER')")
  @GetMapping(value = "/await-accepts", consumes = MediaType.ALL_VALUE)
  public Page<ProductionOrderAwaitAcceptView> awaitAccepts(
    ProductionOrderAwaitAcceptView.Filter filter, Pageable pageable) {
    return productionOrderQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "실행 대기 목록")
  @PreAuthorize("hasAnyRole('PRODUCTION_EXECUTOR', 'PRODUCTION_EXECUTE_MANAGER')")
  @GetMapping(value = "/await-executions", consumes = MediaType.ALL_VALUE)
  public Page<ProductionOrderAwaitExecutionView> awaitOrders(
    ProductionOrderAwaitExecutionView.Filter filter, Pageable pageable) {
    return productionOrderQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "구매 요청 취소")
  @PutMapping("/orders/{id}/cancel")
  @PreAuthorize("hasAnyRole('PRODUCTION_ORDERER', 'PRODUCTION_ORDER_MANAGER')")
  public void cancel(@PathVariable("id") ProductionOrderId id,
    @RequestBody ProductionOrderRequests.CancelRequest request) {
    request.setId(id);
    productionOrderService.cancel(request);
  }

  @ApiOperation(value = "구매 요청 제출")
  @PutMapping("/orders/{id}/commit")
  @PreAuthorize("hasAnyRole('PRODUCTION_ORDERER', 'PRODUCTION_ORDER_MANAGER')")
  public void commit(@PathVariable("id") ProductionOrderId id,
    @RequestBody ProductionOrderRequests.CommitRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setCommitterId(UserId.from(userDetails.getUsername()));
    productionOrderService.commit(request);
  }

  @ApiOperation(value = "구매 요청 완료")
  @PutMapping("/orders/{id}/complete")
  @PreAuthorize("hasAnyRole('PRODUCTION_ORDERER', 'PRODUCTION_ORDER_MANAGER', 'PRODUCTION_EXECUTOR', 'PRODUCTION_EXECUTE_MANAGER')")
  public void complete(@PathVariable("id") ProductionOrderId id,
    @RequestBody ProductionOrderRequests.CompleteRequest request) {
    request.setId(id);
    productionOrderService.complete(request);
  }

  @ApiOperation(value = "구매 요청 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/orders")
  @PreAuthorize("hasAnyRole('PRODUCTION_ORDERER', 'PRODUCTION_ORDER_MANAGER')")
  public ProductionOrderData create(@RequestBody ProductionOrderRequests.CreateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setOrdererId(UserId.from(userDetails.getUsername()));
    return productionOrderService.create(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "구매 요청 조회")
  @PreAuthorize("hasAnyRole('PRODUCTION_ORDERER', 'PRODUCTION_ORDER_ACCEPTER', 'PRODUCTION_ORDER_MANAGER')")
  @GetMapping(value = "/orders/{id}", consumes = MediaType.ALL_VALUE)
  public ProductionOrderData get(@PathVariable("id") ProductionOrderId id) {
    return productionOrderService.get(id);
  }

  /*
  @ApiOperation(value = "구매 요청 삭제")
  @DeleteMapping("/orders/{id}")
  @PreAuthorize("hasRole('PRODUCTION_ORDER_MANAGER')")
  public void delete(@PathVariable("id") ProductionOrderId id) {
    productionOrderService.delete(new ProductionOrderRequests.DeleteRequest(id));
  }
  */

  @ApiOperation(value = "구매 요청 반려")
  @PutMapping("/orders/{id}/reject")
  @PreAuthorize("hasAnyRole('PRODUCTION_ORDER_ACCEPTER', 'PRODUCTION_ORDER_MANAGER')")
  public void reject(@PathVariable("id") ProductionOrderId id,
    @RequestBody ProductionOrderRequests.RejectRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    productionOrderService.reject(request);
  }

  @ApiOperation(value = "구매 요청 검색")
  @PreAuthorize("hasAnyRole('PRODUCTION_ORDERER', 'PRODUCTION_ORDER_MANAGER')")
  @GetMapping(value = "/orders", consumes = MediaType.ALL_VALUE)
  public Page<ProductionOrderView> retrieve(ProductionOrderView.Filter filter,
    Pageable pageable, @AuthenticationPrincipal AuthorizedUser userDetails) {
    val isManager = userDetails.hasRole(Roles.PRODUCTION_ORDER_MANAGER.getId());
    if (!isManager) {
      filter.setOrdererId(UserId.from(userDetails.getUsername()));
    }
    return productionOrderQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "구매 요청 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(ProductionOrderStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "구매 요청 수정")
  @PutMapping("/orders/{id}")
  @PreAuthorize("hasAnyRole('PRODUCTION_ORDERER', 'PRODUCTION_ORDER_MANAGER')")
  public void update(@PathVariable("id") ProductionOrderId id,
    @RequestBody ProductionOrderRequests.UpdateRequest request) {
    request.setId(id);
    productionOrderService.update(request);
  }

}
