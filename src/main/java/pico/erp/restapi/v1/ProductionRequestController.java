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
import pico.erp.production.request.ProductionRequestApi.Roles;
import pico.erp.production.request.ProductionRequestAwaitAcceptView;
import pico.erp.production.request.ProductionRequestData;
import pico.erp.production.request.ProductionRequestId;
import pico.erp.production.request.ProductionRequestQuery;
import pico.erp.production.request.ProductionRequestRequests;
import pico.erp.production.request.ProductionRequestService;
import pico.erp.production.request.ProductionRequestStatusKind;
import pico.erp.production.request.ProductionRequestView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.user.UserId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("production-request-controller-v1")
@RequestMapping(value = "/production-request", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProductionRequestController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private ProductionRequestService productionRequestService;

  @Lazy
  @Autowired
  private ProductionRequestQuery productionRequestQuery;

  @ApiOperation(value = "구매 요청 접수")
  @PutMapping("/requests/{id}/accept")
  @PreAuthorize("hasAnyRole('PRODUCTION_REQUEST_ACCEPTER', 'PRODUCTION_REQUEST_MANAGER')")
  public void accept(@PathVariable("id") ProductionRequestId id,
    @RequestBody ProductionRequestRequests.AcceptRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setAccepterId(UserId.from(userDetails.getUsername()));
    productionRequestService.accept(request);
  }

  @ApiOperation(value = "접수 대기 목록")
  @PreAuthorize("hasAnyRole('PRODUCTION_REQUEST_ACCEPTER', 'PRODUCTION_REQUEST_MANAGER')")
  @GetMapping(value = "/await-accepts", consumes = MediaType.ALL_VALUE)
  public Page<ProductionRequestAwaitAcceptView> awaitAccepts(
    ProductionRequestAwaitAcceptView.Filter filter, Pageable pageable) {
    return productionRequestQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "구매 요청 취소")
  @PutMapping("/requests/{id}/cancel")
  @PreAuthorize("hasAnyRole('PRODUCTION_REQUESTER', 'PRODUCTION_REQUEST_MANAGER')")
  public void cancel(@PathVariable("id") ProductionRequestId id,
    @RequestBody ProductionRequestRequests.CancelRequest request) {
    request.setId(id);
    productionRequestService.cancel(request);
  }

  @ApiOperation(value = "구매 요청 제출")
  @PutMapping("/requests/{id}/commit")
  @PreAuthorize("hasAnyRole('PRODUCTION_REQUESTER', 'PRODUCTION_REQUEST_MANAGER')")
  public void commit(@PathVariable("id") ProductionRequestId id,
    @RequestBody ProductionRequestRequests.CommitRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setCommitterId(UserId.from(userDetails.getUsername()));
    productionRequestService.commit(request);
  }

  @ApiOperation(value = "구매 요청 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/requests")
  @PreAuthorize("hasAnyRole('PRODUCTION_REQUESTER', 'PRODUCTION_REQUEST_MANAGER')")
  public ProductionRequestData create(@RequestBody ProductionRequestRequests.CreateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setRequesterId(UserId.from(userDetails.getUsername()));
    return productionRequestService.create(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "구매 요청 조회")
  @PreAuthorize("hasAnyRole('PRODUCTION_REQUESTER', 'PRODUCTION_REQUEST_ACCEPTER', 'PRODUCTION_REQUEST_MANAGER')")
  @GetMapping(value = "/requests/{id}", consumes = MediaType.ALL_VALUE)
  public ProductionRequestData get(@PathVariable("id") ProductionRequestId id) {
    return productionRequestService.get(id);
  }

  /*
  @ApiOperation(value = "구매 요청 삭제")
  @DeleteMapping("/requests/{id}")
  @PreAuthorize("hasRole('PRODUCTION_REQUEST_MANAGER')")
  public void delete(@PathVariable("id") ProductionRequestId id) {
    productionRequestService.delete(new ProductionRequestRequests.DeleteRequest(id));
  }
  */

  @ApiOperation(value = "구매 요청 검색")
  @PreAuthorize("hasAnyRole('PRODUCTION_REQUESTER', 'PRODUCTION_REQUEST_MANAGER')")
  @GetMapping(value = "/requests", consumes = MediaType.ALL_VALUE)
  public Page<ProductionRequestView> retrieve(ProductionRequestView.Filter filter,
    Pageable pageable, @AuthenticationPrincipal AuthorizedUser userDetails) {
    val isManager = userDetails.hasRole(Roles.PRODUCTION_REQUEST_MANAGER.getId());
    if (!isManager) {
      filter.setRequesterId(UserId.from(userDetails.getUsername()));
    }
    return productionRequestQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "구매 요청 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(ProductionRequestStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "구매 요청 수정")
  @PutMapping("/requests/{id}")
  @PreAuthorize("hasAnyRole('PRODUCTION_REQUESTER', 'PRODUCTION_REQUEST_MANAGER')")
  public void update(@PathVariable("id") ProductionRequestId id,
    @RequestBody ProductionRequestRequests.UpdateRequest request) {
    request.setId(id);
    productionRequestService.update(request);
  }

}
