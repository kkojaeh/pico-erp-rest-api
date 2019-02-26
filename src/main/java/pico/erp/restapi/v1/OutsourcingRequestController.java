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
import pico.erp.outsourcing.request.OutsourcingRequestApi.Roles;
import pico.erp.outsourcing.request.OutsourcingRequestAwaitAcceptView;
import pico.erp.outsourcing.request.OutsourcingRequestAwaitOrderView;
import pico.erp.outsourcing.request.OutsourcingRequestData;
import pico.erp.outsourcing.request.OutsourcingRequestId;
import pico.erp.outsourcing.request.OutsourcingRequestQuery;
import pico.erp.outsourcing.request.OutsourcingRequestRequests;
import pico.erp.outsourcing.request.OutsourcingRequestService;
import pico.erp.outsourcing.request.OutsourcingRequestStatusKind;
import pico.erp.outsourcing.request.OutsourcingRequestView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.UserId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("outsourcing-request-controller-v1")
@RequestMapping(value = "/outsourcing-request", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class OutsourcingRequestController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private OutsourcingRequestService outsourcingRequestService;

  @Lazy
  @Autowired
  private OutsourcingRequestQuery outsourcingRequestQuery;

  @ApiOperation(value = "구매 요청 접수")
  @PutMapping("/requests/{id}/accept")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUEST_ACCEPTER', 'OUTSOURCING_REQUEST_MANAGER')")
  public void accept(@PathVariable("id") OutsourcingRequestId id,
    @RequestBody OutsourcingRequestRequests.AcceptRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setAccepterId(UserId.from(userDetails.getUsername()));
    outsourcingRequestService.accept(request);
  }

  @ApiOperation(value = "접수 대기 목록")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUEST_ACCEPTER', 'OUTSOURCING_REQUEST_MANAGER')")
  @GetMapping(value = "/await-accepts", consumes = MediaType.ALL_VALUE)
  public Page<OutsourcingRequestAwaitAcceptView> awaitAccepts(
    OutsourcingRequestAwaitAcceptView.Filter filter, Pageable pageable) {
    return outsourcingRequestQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "발주 대기 목록")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  @GetMapping(value = "/await-orders", consumes = MediaType.ALL_VALUE)
  public Page<OutsourcingRequestAwaitOrderView> awaitOrders(
    OutsourcingRequestAwaitOrderView.Filter filter, Pageable pageable) {
    return outsourcingRequestQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "구매 요청 취소")
  @PutMapping("/requests/{id}/cancel")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUESTER', 'OUTSOURCING_REQUEST_MANAGER')")
  public void cancel(@PathVariable("id") OutsourcingRequestId id,
    @RequestBody OutsourcingRequestRequests.CancelRequest request) {
    request.setId(id);
    outsourcingRequestService.cancel(request);
  }

  @ApiOperation(value = "구매 요청 제출")
  @PutMapping("/requests/{id}/commit")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUESTER', 'OUTSOURCING_REQUEST_MANAGER')")
  public void commit(@PathVariable("id") OutsourcingRequestId id,
    @RequestBody OutsourcingRequestRequests.CommitRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setCommitterId(UserId.from(userDetails.getUsername()));
    outsourcingRequestService.commit(request);
  }

  @ApiOperation(value = "구매 요청 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/requests")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUESTER', 'OUTSOURCING_REQUEST_MANAGER')")
  public OutsourcingRequestData create(
    @RequestBody OutsourcingRequestRequests.CreateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setRequesterId(UserId.from(userDetails.getUsername()));
    return outsourcingRequestService.create(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "구매 요청 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUESTER', 'OUTSOURCING_REQUEST_ACCEPTER', 'OUTSOURCING_REQUEST_MANAGER')")
  @GetMapping(value = "/requests/{id}", consumes = MediaType.ALL_VALUE)
  public OutsourcingRequestData get(@PathVariable("id") OutsourcingRequestId id) {
    return outsourcingRequestService.get(id);
  }

  /*
  @ApiOperation(value = "구매 요청 삭제")
  @DeleteMapping("/requests/{id}")
  @PreAuthorize("hasRole('OUTSOURCING_REQUEST_MANAGER')")
  public void delete(@PathVariable("id") OutsourcingRequestId id) {
    outsourcingRequestService.delete(new OutsourcingRequestRequests.DeleteRequest(id));
  }
  */

  @ApiOperation(value = "구매 요청 반려")
  @PutMapping("/requests/{id}/reject")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUEST_ACCEPTER', 'OUTSOURCING_REQUEST_MANAGER')")
  public void reject(@PathVariable("id") OutsourcingRequestId id,
    @RequestBody OutsourcingRequestRequests.RejectRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    outsourcingRequestService.reject(request);
  }

  @ApiOperation(value = "구매 요청 검색")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUESTER', 'OUTSOURCING_REQUEST_MANAGER')")
  @GetMapping(value = "/requests", consumes = MediaType.ALL_VALUE)
  public Page<OutsourcingRequestView> retrieve(OutsourcingRequestView.Filter filter,
    Pageable pageable, @AuthenticationPrincipal AuthorizedUser userDetails) {
    val isManager = userDetails.hasRole(Roles.OUTSOURCING_REQUEST_MANAGER.getId());
    if (!isManager) {
      filter.setRequesterId(UserId.from(userDetails.getUsername()));
    }
    return outsourcingRequestQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "구매 요청 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(OutsourcingRequestStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "구매 요청 수정")
  @PutMapping("/requests/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUESTER', 'OUTSOURCING_REQUEST_MANAGER')")
  public void update(@PathVariable("id") OutsourcingRequestId id,
    @RequestBody OutsourcingRequestRequests.UpdateRequest request) {
    request.setId(id);
    outsourcingRequestService.update(request);
  }

}
