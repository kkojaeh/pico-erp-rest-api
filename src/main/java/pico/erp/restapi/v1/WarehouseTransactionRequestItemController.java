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
import pico.erp.restapi.Versions;
import pico.erp.warehouse.transaction.request.TransactionRequestId;
import pico.erp.warehouse.transaction.request.item.TransactionRequestItemData;
import pico.erp.warehouse.transaction.request.item.TransactionRequestItemId;
import pico.erp.warehouse.transaction.request.item.TransactionRequestItemRequests;
import pico.erp.warehouse.transaction.request.item.TransactionRequestItemService;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("warehouse-transaction-request-item-controller-v1")
@RequestMapping(value = "/warehouse", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WarehouseTransactionRequestItemController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private TransactionRequestItemService transactionRequestItemService;


  @ApiOperation(value = "프로젝트 판매 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/transaction-requests/{requestId}/items")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  public TransactionRequestItemData create(
    @PathVariable("requestId") TransactionRequestId requestId,
    @RequestBody TransactionRequestItemRequests.CreateRequest request) {
    request.setRequestId(requestId);
    return transactionRequestItemService.create(request);
  }


  @ApiOperation(value = "프로젝트 판매 품목 삭제")
  @DeleteMapping("/transaction-requests/{requestId}/items/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  public void delete(@PathVariable("requestId") TransactionRequestId requestId,
    @PathVariable("id") TransactionRequestItemId id) {
    transactionRequestItemService.delete(new TransactionRequestItemRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "프로젝트 판매 품목 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  @GetMapping(value = "/transaction-requests/{requestId}/items", consumes = MediaType.ALL_VALUE)
  public List<TransactionRequestItemData> getAll(
    @PathVariable("requestId") TransactionRequestId requestId) {
    return transactionRequestItemService.getAll(requestId);
  }

  @ApiOperation(value = "프로젝트 판매 품목 수정")
  @PutMapping("/transaction-requests/{requestId}/items/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  public void update(@PathVariable("requestId") TransactionRequestId requestId,
    @PathVariable("id") TransactionRequestItemId id,
    @RequestBody TransactionRequestItemRequests.UpdateRequest request) {
    request.setId(id);
    transactionRequestItemService.update(request);
  }

}
