package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import pico.erp.purchase.order.PurchaseOrderId;
import pico.erp.purchase.order.item.PurchaseOrderItemData;
import pico.erp.purchase.order.item.PurchaseOrderItemId;
import pico.erp.purchase.order.item.PurchaseOrderItemRequests;
import pico.erp.purchase.order.item.PurchaseOrderItemService;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("purchase-order-item-controller-v1")
@RequestMapping(value = "/purchase-order", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class PurchaseOrderItemController {

  @Lazy
  @Autowired
  private PurchaseOrderItemService purchaseOrderItemService;

  @ApiOperation(value = "발주 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/orders/{orderId}/items")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  public PurchaseOrderItemData create(@PathVariable("orderId") PurchaseOrderId orderId,
    @RequestBody PurchaseOrderItemRequests.CreateRequest request) {
    request.setOrderId(orderId);
    return purchaseOrderItemService.create(request);
  }

  @ApiOperation(value = "발주 품목 삭제")
  @DeleteMapping("/orders/{orderId}/items/{id}")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  public void delete(@PathVariable("orderId") PurchaseOrderId orderId,
    @PathVariable("id") PurchaseOrderItemId id) {
    purchaseOrderItemService.delete(new PurchaseOrderItemRequests.DeleteRequest(id));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "발주 품목 조회")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  @GetMapping(value = "/items/{id}", consumes = MediaType.ALL_VALUE)
  public PurchaseOrderItemData get(@PathVariable("id") PurchaseOrderItemId id) {
    return purchaseOrderItemService.get(id);
  }

  @ApiOperation(value = "발주 품목 조회")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  @GetMapping(value = "/orders/{orderId}/items", consumes = MediaType.ALL_VALUE)
  public List<PurchaseOrderItemData> getAll(
    @PathVariable("orderId") PurchaseOrderId orderId) {
    return purchaseOrderItemService.getAll(orderId);
  }

  @ApiOperation(value = "발주 품목 수정")
  @PutMapping("/orders/{orderId}/items/{id}")
  @PreAuthorize("hasAnyRole('PURCHASE_ORDER_CHARGER', 'PURCHASE_ORDER_MANAGER')")
  public void update(@PathVariable("orderId") PurchaseOrderId orderId,
    @PathVariable("id") PurchaseOrderItemId id,
    @RequestBody PurchaseOrderItemRequests.UpdateRequest request) {
    request.setId(id);
    purchaseOrderItemService.update(request);
  }

}
