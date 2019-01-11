package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import pico.erp.purchase.order.PurchaseOrderData;
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.purchase.order.PurchaseOrderQuery;
import pico.erp.purchase.order.PurchaseOrderRequests;
import pico.erp.purchase.order.PurchaseOrderService;
import pico.erp.purchase.order.PurchaseOrderStatusKind;
import pico.erp.purchase.order.PurchaseOrderView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.UserId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("purchase-order-controller-v1")
@RequestMapping(value = "/purchase-order", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class PurchaseOrderController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private PurchaseOrderService purchaseOrderService;

  @Lazy
  @Autowired
  private PurchaseOrderQuery purchaseOrderQuery;

  @ApiOperation(value = "발주 취소")
  @PutMapping("/orders/{id}/cancel")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  public void cancel(@PathVariable("id") PurchaseOrderId id,
    @RequestBody PurchaseOrderRequests.CancelRequest request) {
    request.setId(id);
    purchaseOrderService.cancel(request);
  }

  @ApiOperation(value = "발주 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/orders")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  public PurchaseOrderData create(@RequestBody PurchaseOrderRequests.CreateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setChargerId(UserId.from(userDetails.getUsername()));
    return purchaseOrderService.create(request);
  }

  @ApiOperation(value = "발주 확정")
  @PutMapping("/orders/{id}/determine")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  public void determine(@PathVariable("id") PurchaseOrderId id,
    @RequestBody PurchaseOrderRequests.DetermineRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    purchaseOrderService.determine(request);
  }

  @ApiOperation(value = "발주 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/orders/{id}/generate")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  public PurchaseOrderData generate(
    @PathVariable("id") PurchaseOrderId id,
    @RequestBody PurchaseOrderRequests.GenerateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setChargerId(UserId.from(userDetails.getUsername()));
    return purchaseOrderService.generate(request);
  }

  @ApiOperation(value = "발주 조회")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  @GetMapping(value = "/orders/{id}", consumes = MediaType.ALL_VALUE)
  public PurchaseOrderData get(@PathVariable("id") PurchaseOrderId id) {
    return purchaseOrderService.get(id);
  }

  @SneakyThrows
  @ApiOperation(value = "발주서 다운로드")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  @GetMapping(value = "/orders/{id}/print-draft", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> printDraft(@PathVariable("id") PurchaseOrderId id,
    PurchaseOrderRequests.PrintDraftRequest request) {
    request.setId(id);
    return SharedController.asResponse(purchaseOrderService.printDraft(
      request
    ));
  }

  @ApiOperation(value = "발주 반려")
  @PutMapping("/orders/{id}/reject")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  public void reject(@PathVariable("id") PurchaseOrderId id,
    @RequestBody PurchaseOrderRequests.RejectRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    purchaseOrderService.reject(request);
  }

  /*
  @ApiOperation(value = "발주 삭제")
  @DeleteMapping("/orders/{id}")
  @PreAuthorize("hasRole('PURCHASE_ORDER_MANAGER')")
  public void delete(@PathVariable("id") PurchaseOrderId id) {
    purchaseOrderService.delete(new PurchaseOrderRequests.DeleteRequest(id));
  }
  */

  @ApiOperation(value = "발주 검색")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  @GetMapping(value = "/orders", consumes = MediaType.ALL_VALUE)
  public Page<PurchaseOrderView> retrieve(PurchaseOrderView.Filter filter,
    Pageable pageable, @AuthenticationPrincipal AuthorizedUser userDetails) {
    return purchaseOrderQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "발주 전송")
  @PutMapping("/orders/{id}/send")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  public void send(@PathVariable("id") PurchaseOrderId id,
    @RequestBody PurchaseOrderRequests.SendRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    purchaseOrderService.send(request);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "발주 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(PurchaseOrderStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "발주 수정")
  @PutMapping("/orders/{id}")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  public void update(@PathVariable("id") PurchaseOrderId id,
    @RequestBody PurchaseOrderRequests.UpdateRequest request) {
    request.setId(id);
    purchaseOrderService.update(request);
  }

}
