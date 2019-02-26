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
import pico.erp.outsourcing.order.OutsourcingOrderData;
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.outsourcing.order.OutsourcingOrderQuery;
import pico.erp.outsourcing.order.OutsourcingOrderRequests;
import pico.erp.outsourcing.order.OutsourcingOrderService;
import pico.erp.outsourcing.order.OutsourcingOrderStatusKind;
import pico.erp.outsourcing.order.OutsourcingOrderView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.UserId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("outsourcing-order-controller-v1")
@RequestMapping(value = "/outsourcing-order", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class OutsourcingOrderController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private OutsourcingOrderService outsourcingOrderService;

  @Lazy
  @Autowired
  private OutsourcingOrderQuery outsourcingOrderQuery;

  @ApiOperation(value = "발주 취소")
  @PutMapping("/orders/{id}/cancel")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public void cancel(@PathVariable("id") OutsourcingOrderId id,
    @RequestBody OutsourcingOrderRequests.CancelRequest request) {
    request.setId(id);
    outsourcingOrderService.cancel(request);
  }

  @ApiOperation(value = "발주 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/orders")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public OutsourcingOrderData create(@RequestBody OutsourcingOrderRequests.CreateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setChargerId(UserId.from(userDetails.getUsername()));
    return outsourcingOrderService.create(request);
  }

  @ApiOperation(value = "발주 확정")
  @PutMapping("/orders/{id}/determine")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public void determine(@PathVariable("id") OutsourcingOrderId id,
    @RequestBody OutsourcingOrderRequests.DetermineRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    outsourcingOrderService.determine(request);
  }

  @ApiOperation(value = "발주 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/orders/{id}/generate")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public OutsourcingOrderData generate(
    @PathVariable("id") OutsourcingOrderId id,
    @RequestBody OutsourcingOrderRequests.GenerateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setChargerId(UserId.from(userDetails.getUsername()));
    return outsourcingOrderService.generate(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "발주 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  @GetMapping(value = "/orders/{id}", consumes = MediaType.ALL_VALUE)
  public OutsourcingOrderData get(@PathVariable("id") OutsourcingOrderId id) {
    return outsourcingOrderService.get(id);
  }

  @ApiOperation(value = "발주 반려")
  @PutMapping("/orders/{id}/reject")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public void reject(@PathVariable("id") OutsourcingOrderId id,
    @RequestBody OutsourcingOrderRequests.RejectRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    outsourcingOrderService.reject(request);
  }

  /*
  @ApiOperation(value = "발주 삭제")
  @DeleteMapping("/orders/{id}")
  @PreAuthorize("hasRole('OUTSOURCING_ORDER_MANAGER')")
  public void delete(@PathVariable("id") OutsourcingOrderId id) {
    outsourcingOrderService.delete(new OutsourcingOrderRequests.DeleteRequest(id));
  }
  */

  @ApiOperation(value = "발주 검색")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  @GetMapping(value = "/orders", consumes = MediaType.ALL_VALUE)
  public Page<OutsourcingOrderView> retrieve(OutsourcingOrderView.Filter filter,
    Pageable pageable, @AuthenticationPrincipal AuthorizedUser userDetails) {
    return outsourcingOrderQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "발주 전송")
  @PutMapping("/orders/{id}/send")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public void send(@PathVariable("id") OutsourcingOrderId id,
    @RequestBody OutsourcingOrderRequests.SendRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    outsourcingOrderService.send(request);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "발주 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(OutsourcingOrderStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "발주 수정")
  @PutMapping("/orders/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public void update(@PathVariable("id") OutsourcingOrderId id,
    @RequestBody OutsourcingOrderRequests.UpdateRequest request) {
    request.setId(id);
    outsourcingOrderService.update(request);
  }

}
