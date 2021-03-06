package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import pico.erp.order.acceptance.OrderAcceptanceId;
import pico.erp.order.acceptance.item.OrderAcceptanceItemData;
import pico.erp.order.acceptance.item.OrderAcceptanceItemId;
import pico.erp.order.acceptance.item.OrderAcceptanceItemRequests;
import pico.erp.order.acceptance.item.OrderAcceptanceItemService;
import pico.erp.rest.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("order-acceptance-item-controller-v1")
@RequestMapping(value = "/order-acceptance", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class OrderAcceptanceItemController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @ComponentAutowired
  private OrderAcceptanceItemService orderAcceptanceItemService;

  @ApiOperation(value = "주문 접수 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/acceptances/{orderAcceptanceId}/items")
  @PreAuthorize("hasRole('ORDER_ACCEPTANCE_MANAGER')")
  public OrderAcceptanceItemData create(
    @PathVariable("orderAcceptanceId") OrderAcceptanceId orderAcceptanceId,
    @RequestBody OrderAcceptanceItemRequests.CreateRequest request) {
    request.setOrderAcceptanceId(orderAcceptanceId);
    return orderAcceptanceItemService.create(request);
  }

  @ApiOperation(value = "주문 접수 품목 삭제")
  @DeleteMapping("/acceptances/{orderAcceptanceId}/items/{id}")
  @PreAuthorize("hasRole('ORDER_ACCEPTANCE_MANAGER')")
  public void delete(@PathVariable("orderAcceptanceId") OrderAcceptanceId orderAcceptanceId,
    @PathVariable("id") OrderAcceptanceItemId id) {
    orderAcceptanceItemService.delete(new OrderAcceptanceItemRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "주문 접수 품목 조회")
  @PreAuthorize("hasAnyRole('ORDER_ACCEPTANCE_MANAGER', 'ORDER_ACCEPTANCE_ACCESSOR')")
  @GetMapping(value = "/acceptances/{orderAcceptanceId}/items", consumes = MediaType.ALL_VALUE)
  public List<OrderAcceptanceItemData> getAll(
    @PathVariable("orderAcceptanceId") OrderAcceptanceId orderAcceptanceId) {
    return orderAcceptanceItemService.getAll(orderAcceptanceId);
  }

  @ApiOperation(value = "주문 접수 품목 수정")
  @PutMapping("/acceptances/{orderAcceptanceId}/items/{id}")
  @PreAuthorize("hasRole('ORDER_ACCEPTANCE_MANAGER')")
  public void update(@PathVariable("orderAcceptanceId") OrderAcceptanceId orderAcceptanceId,
    @PathVariable("id") OrderAcceptanceItemId id,
    @RequestBody OrderAcceptanceItemRequests.UpdateRequest request) {
    request.setId(id);
    orderAcceptanceItemService.update(request);
  }

}
