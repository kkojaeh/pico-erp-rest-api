package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
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
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemData;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemId;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemRequests;
import pico.erp.outsourcing.order.item.OutsourcingOrderItemService;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("outsourcing-order-item-controller-v1")
@RequestMapping(value = "/outsourcing-order", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class OutsourcingOrderItemController {

  @ComponentAutowired
  private OutsourcingOrderItemService outsourcingOrderItemService;

  @ApiOperation(value = "발주 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/orders/{orderId}/items")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public OutsourcingOrderItemData create(@PathVariable("orderId") OutsourcingOrderId orderId,
    @RequestBody OutsourcingOrderItemRequests.CreateRequest request) {
    request.setOrderId(orderId);
    return outsourcingOrderItemService.create(request);
  }

  @ApiOperation(value = "발주 품목 삭제")
  @DeleteMapping("/orders/{orderId}/items/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public void delete(@PathVariable("orderId") OutsourcingOrderId orderId,
    @PathVariable("id") OutsourcingOrderItemId id) {
    outsourcingOrderItemService.delete(new OutsourcingOrderItemRequests.DeleteRequest(id));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "발주 품목 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  @GetMapping(value = "/items/{id}", consumes = MediaType.ALL_VALUE)
  public OutsourcingOrderItemData get(@PathVariable("id") OutsourcingOrderItemId id) {
    return outsourcingOrderItemService.get(id);
  }

  @ApiOperation(value = "발주 품목 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  @GetMapping(value = "/orders/{orderId}/items", consumes = MediaType.ALL_VALUE)
  public List<OutsourcingOrderItemData> getAll(
    @PathVariable("orderId") OutsourcingOrderId orderId) {
    return outsourcingOrderItemService.getAll(orderId);
  }

  @ApiOperation(value = "발주 품목 수정")
  @PutMapping("/orders/{orderId}/items/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public void update(@PathVariable("orderId") OutsourcingOrderId orderId,
    @PathVariable("id") OutsourcingOrderItemId id,
    @RequestBody OutsourcingOrderItemRequests.UpdateRequest request) {
    request.setId(id);
    outsourcingOrderItemService.update(request);
  }

}
