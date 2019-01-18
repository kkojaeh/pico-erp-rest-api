package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import pico.erp.purchase.request.PurchaseRequestId;
import pico.erp.purchase.request.item.PurchaseRequestItemData;
import pico.erp.purchase.request.item.PurchaseRequestItemId;
import pico.erp.purchase.request.item.PurchaseRequestItemRequests;
import pico.erp.purchase.request.item.PurchaseRequestItemService;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("purchase-request-item-controller-v1")
@RequestMapping(value = "/purchase-request", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class PurchaseRequestItemController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private PurchaseRequestItemService purchaseRequestItemService;

  @ApiOperation(value = "구매 요청 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/requests/{requestId}/items")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUESTER', 'PURCHASE_REQUEST_MANAGER')")
  public PurchaseRequestItemData create(@PathVariable("requestId") PurchaseRequestId requestId,
    @RequestBody PurchaseRequestItemRequests.CreateRequest request) {
    request.setRequestId(requestId);
    return purchaseRequestItemService.create(request);
  }

  @ApiOperation(value = "구매 요청 품목 삭제")
  @DeleteMapping("/requests/{requestId}/items/{id}")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUESTER', 'PURCHASE_REQUEST_MANAGER')")
  public void delete(@PathVariable("requestId") PurchaseRequestId requestId,
    @PathVariable("id") PurchaseRequestItemId id) {
    purchaseRequestItemService.delete(new PurchaseRequestItemRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "구매 요청 품목 조회")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUESTER', 'PURCHASE_REQUEST_MANAGER', 'PURCHASE_REQUEST_ACCEPTER')")
  @GetMapping(value = "/requests/{requestId}/items", consumes = MediaType.ALL_VALUE)
  public List<PurchaseRequestItemData> getAll(
    @PathVariable("requestId") PurchaseRequestId requestId) {
    return purchaseRequestItemService.getAll(requestId);
  }

  @ApiOperation(value = "구매 요청 품목 수정")
  @PutMapping("/requests/{requestId}/items/{id}")
  @PreAuthorize("hasAnyRole('PURCHASE_REQUESTER', 'PURCHASE_REQUEST_MANAGER')")
  public void update(@PathVariable("requestId") PurchaseRequestId requestId,
    @PathVariable("id") PurchaseRequestItemId id,
    @RequestBody PurchaseRequestItemRequests.UpdateRequest request) {
    request.setId(id);
    purchaseRequestItemService.update(request);
  }

}
