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
import pico.erp.outsourcing.order.OutsourcingOrderId;
import pico.erp.outsourcing.order.material.OutsourcingOrderMaterialData;
import pico.erp.outsourcing.order.material.OutsourcingOrderMaterialId;
import pico.erp.outsourcing.order.material.OutsourcingOrderMaterialRequests;
import pico.erp.outsourcing.order.material.OutsourcingOrderMaterialService;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("outsourcing-order-material-controller-v1")
@RequestMapping(value = "/outsourcing-order", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class OutsourcingOrderMaterialController {

  @Lazy
  @Autowired
  private OutsourcingOrderMaterialService outsourcingOrderMaterialService;

  @ApiOperation(value = "발주 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/orders/{orderId}/materials")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public OutsourcingOrderMaterialData create(@PathVariable("orderId") OutsourcingOrderId orderId,
    @RequestBody OutsourcingOrderMaterialRequests.CreateRequest request) {
    request.setOrderId(orderId);
    return outsourcingOrderMaterialService.create(request);
  }

  @ApiOperation(value = "발주 품목 삭제")
  @DeleteMapping("/orders/{orderId}/materials/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public void delete(@PathVariable("orderId") OutsourcingOrderId orderId,
    @PathVariable("id") OutsourcingOrderMaterialId id) {
    outsourcingOrderMaterialService.delete(new OutsourcingOrderMaterialRequests.DeleteRequest(id));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "발주 품목 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  @GetMapping(value = "/materials/{id}", consumes = MediaType.ALL_VALUE)
  public OutsourcingOrderMaterialData get(@PathVariable("id") OutsourcingOrderMaterialId id) {
    return outsourcingOrderMaterialService.get(id);
  }

  @ApiOperation(value = "발주 품목 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  @GetMapping(value = "/orders/{orderId}/materials", consumes = MediaType.ALL_VALUE)
  public List<OutsourcingOrderMaterialData> getAll(
    @PathVariable("orderId") OutsourcingOrderId orderId) {
    return outsourcingOrderMaterialService.getAll(orderId);
  }

  @ApiOperation(value = "발주 품목 수정")
  @PutMapping("/orders/{orderId}/materials/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_ORDER_CHARGER', 'OUTSOURCING_ORDER_MANAGER')")
  public void update(@PathVariable("orderId") OutsourcingOrderId orderId,
    @PathVariable("id") OutsourcingOrderMaterialId id,
    @RequestBody OutsourcingOrderMaterialRequests.UpdateRequest request) {
    request.setId(id);
    outsourcingOrderMaterialService.update(request);
  }

}
