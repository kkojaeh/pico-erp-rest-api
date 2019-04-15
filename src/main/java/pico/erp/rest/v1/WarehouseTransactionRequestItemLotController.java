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
import pico.erp.rest.Versions;
import pico.erp.warehouse.transaction.request.TransactionRequestId;
import pico.erp.warehouse.transaction.request.item.TransactionRequestItemId;
import pico.erp.warehouse.transaction.request.item.lot.TransactionRequestItemLotData;
import pico.erp.warehouse.transaction.request.item.lot.TransactionRequestItemLotId;
import pico.erp.warehouse.transaction.request.item.lot.TransactionRequestItemLotRequests;
import pico.erp.warehouse.transaction.request.item.lot.TransactionRequestItemLotService;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("warehouse-transaction-request-item-lot-controller-v1")
@RequestMapping(value = "/warehouse", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WarehouseTransactionRequestItemLotController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @ComponentAutowired
  private TransactionRequestItemLotService transactionRequestItemLotService;


  @ApiOperation(value = "프로젝트 판매 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/transaction-requests/{requestId}/items/{requestItemId}/lots")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  public TransactionRequestItemLotData create(
    @PathVariable("requestId") TransactionRequestId requestId,
    @PathVariable("requestItemId") TransactionRequestItemId requestItemId,
    @RequestBody TransactionRequestItemLotRequests.CreateRequest request) {
    request.setRequestItemId(requestItemId);
    return transactionRequestItemLotService.create(request);
  }


  @ApiOperation(value = "프로젝트 판매 품목 삭제")
  @DeleteMapping("/transaction-requests/{requestId}/items/{requestItemId}/lots/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  public void delete(
    @PathVariable("requestId") TransactionRequestId requestId,
    @PathVariable("requestItemId") TransactionRequestItemId requestItemId,
    @PathVariable("id") TransactionRequestItemLotId id
  ) {
    transactionRequestItemLotService
      .delete(new TransactionRequestItemLotRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "프로젝트 판매 품목 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  @GetMapping(value = "/transaction-requests/{requestId}/items/{requestItemId}/lots", consumes = MediaType.ALL_VALUE)
  public List<TransactionRequestItemLotData> getAll(
    @PathVariable("requestId") TransactionRequestId requestId,
    @PathVariable("requestItemId") TransactionRequestItemId requestItemId
  ) {
    return transactionRequestItemLotService.getAll(requestItemId);
  }

  @ApiOperation(value = "프로젝트 판매 품목 수정")
  @PutMapping("/transaction-requests/{requestId}/items/{requestItemId}/lots/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  public void update(
    @PathVariable("requestId") TransactionRequestId requestId,
    @PathVariable("requestItemId") TransactionRequestItemId requestItemId,
    @PathVariable("id") TransactionRequestItemLotId id,
    @RequestBody TransactionRequestItemLotRequests.UpdateRequest request) {
    request.setId(id);
    transactionRequestItemLotService.update(request);
  }

}
