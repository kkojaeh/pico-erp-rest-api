package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
import pico.erp.purchase.request.PurchaseRequestApi.Roles;
import pico.erp.purchase.request.PurchaseRequestAwaitAcceptView;
import pico.erp.purchase.request.PurchaseRequestAwaitOrderView;
import pico.erp.purchase.request.PurchaseRequestData;
import pico.erp.purchase.request.PurchaseRequestId;
import pico.erp.purchase.request.PurchaseRequestQuery;
import pico.erp.purchase.request.PurchaseRequestRequests;
import pico.erp.purchase.request.PurchaseRequestService;
import pico.erp.purchase.request.PurchaseRequestStatusKind;
import pico.erp.purchase.request.PurchaseRequestView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.UserId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("purchase-request-controller-v1")
@RequestMapping(value = "/purchase-request", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class PurchaseRequestController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private PurchaseRequestService purchaseRequestService;

  @Lazy
  @Autowired
  private PurchaseRequestQuery purchaseRequestQuery;

  @ApiOperation(value = "구매 요청 접수")
  @PutMapping("/requests/{id}/accept")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUEST_ACCEPTER', 'PURCHASE_REQUEST_MANAGER')")
  public void accept(@PathVariable("id") PurchaseRequestId id,
    @RequestBody PurchaseRequestRequests.AcceptRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setAccepterId(UserId.from(userDetails.getUsername()));
    purchaseRequestService.accept(request);
  }

  @ApiOperation(value = "접수 대기 목록")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUEST_ACCEPTER', 'PURCHASE_REQUEST_MANAGER')")
  @GetMapping(value = "/await-accepts", consumes = MediaType.ALL_VALUE)
  public Page<PurchaseRequestAwaitAcceptView> awaitAccepts(
    PurchaseRequestAwaitAcceptView.Filter filter, Pageable pageable) {
    return purchaseRequestQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "발주 대기 목록")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  @GetMapping(value = "/await-orders", consumes = MediaType.ALL_VALUE)
  public Page<PurchaseRequestAwaitOrderView> awaitOrders(
    PurchaseRequestAwaitOrderView.Filter filter, Pageable pageable) {
    return purchaseRequestQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "구매 요청 취소")
  @PutMapping("/requests/{id}/cancel")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUESTER', 'PURCHASE_REQUEST_MANAGER')")
  public void cancel(@PathVariable("id") PurchaseRequestId id,
    @RequestBody PurchaseRequestRequests.CancelRequest request) {
    request.setId(id);
    purchaseRequestService.cancel(request);
  }

  @ApiOperation(value = "구매 요청 제출")
  @PutMapping("/requests/{id}/commit")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUESTER', 'PURCHASE_REQUEST_MANAGER')")
  public void commit(@PathVariable("id") PurchaseRequestId id,
    @RequestBody PurchaseRequestRequests.CommitRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setCommitterId(UserId.from(userDetails.getUsername()));
    purchaseRequestService.commit(request);
  }

  @ApiOperation(value = "구매 요청 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/requests")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUESTER', 'PURCHASE_REQUEST_MANAGER')")
  public PurchaseRequestData create(@RequestBody PurchaseRequestRequests.CreateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setRequesterId(UserId.from(userDetails.getUsername()));
    return purchaseRequestService.create(request);
  }

  @ApiOperation(value = "구매 요청 조회")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUESTER', 'PURCHASE_REQUEST_ACCEPTER', 'PURCHASE_REQUEST_MANAGER')")
  @GetMapping(value = "/requests/{id}", consumes = MediaType.ALL_VALUE)
  public PurchaseRequestData get(@PathVariable("id") PurchaseRequestId id) {
    return purchaseRequestService.get(id);
  }

  /*
  @ApiOperation(value = "구매 요청 삭제")
  @DeleteMapping("/requests/{id}")
  @PreAuthorize("hasRole('PURCHASE_REQUEST_MANAGER')")
  public void delete(@PathVariable("id") PurchaseRequestId id) {
    purchaseRequestService.delete(new PurchaseRequestRequests.DeleteRequest(id));
  }
  */

  @ApiOperation(value = "구매 요청 반려")
  @PutMapping("/requests/{id}/reject")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUEST_ACCEPTER', 'PURCHASE_REQUEST_MANAGER')")
  public void reject(@PathVariable("id") PurchaseRequestId id,
    @RequestBody PurchaseRequestRequests.RejectRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    purchaseRequestService.reject(request);
  }

  @ApiOperation(value = "구매 요청 검색")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUESTER', 'PURCHASE_REQUEST_MANAGER')")
  @GetMapping(value = "/requests", consumes = MediaType.ALL_VALUE)
  public Page<PurchaseRequestView> retrieve(PurchaseRequestView.Filter filter,
    Pageable pageable, @AuthenticationPrincipal AuthorizedUser userDetails) {
    val isManager = userDetails.hasRole(Roles.PURCHASE_REQUEST_MANAGER.getId());
    if (!isManager) {
      filter.setRequesterId(UserId.from(userDetails.getUsername()));
    }
    return purchaseRequestQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "구매 요청 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(PurchaseRequestStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "구매 요청 수정")
  @PutMapping("/requests/{id}")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUESTER', 'PURCHASE_REQUEST_MANAGER')")
  public void update(@PathVariable("id") PurchaseRequestId id,
    @RequestBody PurchaseRequestRequests.UpdateRequest request) {
    request.setId(id);
    purchaseRequestService.update(request);
  }

}
