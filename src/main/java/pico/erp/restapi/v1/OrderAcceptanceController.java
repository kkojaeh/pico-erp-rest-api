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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.order.acceptance.OrderAcceptanceData;
import pico.erp.order.acceptance.OrderAcceptanceId;
import pico.erp.order.acceptance.OrderAcceptanceQuery;
import pico.erp.order.acceptance.OrderAcceptanceRequests;
import pico.erp.order.acceptance.OrderAcceptanceService;
import pico.erp.order.acceptance.OrderAcceptanceStatusKind;
import pico.erp.order.acceptance.OrderAcceptanceView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("order-acceptance-controller-v1")
@RequestMapping(value = "/order-acceptance", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class OrderAcceptanceController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private OrderAcceptanceService orderAcceptanceService;

  @Lazy
  @Autowired
  private OrderAcceptanceQuery orderAcceptanceQuery;

  @ApiOperation(value = "주문 접수")
  @PutMapping("/acceptances/{id}/accept")
  @PreAuthorize("hasRole('ORDER_ACCEPTANCE_MANAGER')")
  public void accept(@PathVariable("id") OrderAcceptanceId id,
    @RequestBody OrderAcceptanceRequests.AcceptRequest request) {
    request.setId(id);
    orderAcceptanceService.accept(request);
  }

  @ApiOperation(value = "주문 접수 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/acceptances")
  @PreAuthorize("hasRole('ORDER_ACCEPTANCE_MANAGER')")
  public OrderAcceptanceData create(@RequestBody OrderAcceptanceRequests.CreateRequest request) {
    return orderAcceptanceService.create(request);
  }

  @ApiOperation(value = "주문 접수 삭제")
  @DeleteMapping("/acceptances/{id}")
  @PreAuthorize("hasRole('ORDER_ACCEPTANCE_MANAGER')")
  public void delete(@PathVariable("id") OrderAcceptanceId id) {
    orderAcceptanceService.delete(new OrderAcceptanceRequests.DeleteRequest(id));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "주문 접수 조회")
  @PreAuthorize("hasAnyRole('ORDER_ACCEPTANCE_MANAGER', 'ORDER_ACCEPTANCE_ACCESSOR')")
  @GetMapping(value = "/acceptances/{id}", consumes = MediaType.ALL_VALUE)
  public OrderAcceptanceData get(@PathVariable("id") OrderAcceptanceId id) {
    return orderAcceptanceService.get(id);
  }

  @ApiOperation(value = "주문 접수 검색")
  @PreAuthorize("hasAnyRole('ORDER_ACCEPTANCE_MANAGER', 'ORDER_ACCEPTANCE_ACCESSOR')")
  @GetMapping(value = "/acceptances", consumes = MediaType.ALL_VALUE)
  public Page<OrderAcceptanceView> retrieve(
    @ModelAttribute OrderAcceptanceView.Filter filter,
    Pageable pageable) {
    return orderAcceptanceQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "주문 접수 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(OrderAcceptanceStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @ApiOperation(value = "주문 접수 수정")
  @PutMapping("/acceptances/{id}")
  @PreAuthorize("hasRole('ORDER_ACCEPTANCE_MANAGER')")
  public void update(@PathVariable("id") OrderAcceptanceId id,
    @RequestBody OrderAcceptanceRequests.UpdateRequest request) {
    request.setId(id);
    orderAcceptanceService.update(request);
  }

}
