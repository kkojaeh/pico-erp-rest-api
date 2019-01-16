package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
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
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.warehouse.transaction.request.TransactionRequestData;
import pico.erp.warehouse.transaction.request.TransactionRequestId;
import pico.erp.warehouse.transaction.request.TransactionRequestQuery;
import pico.erp.warehouse.transaction.request.TransactionRequestRequests;
import pico.erp.warehouse.transaction.request.TransactionRequestService;
import pico.erp.warehouse.transaction.request.TransactionRequestView;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("warehouse-transaction-request-controller-v1")
@RequestMapping(value = "/warehouse", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WarehouseTransactionRequestController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private TransactionRequestService transactionRequestService;

  @Lazy
  @Autowired
  private TransactionRequestQuery transactionRequestQuery;

  @ApiOperation(value = "입/출고 생성")
  @PutMapping("/transaction-requests/{id}/cancel")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  public void cancel(@PathVariable("id") TransactionRequestId id,
    @RequestBody TransactionRequestRequests.CommitRequest request) {
    request.setId(id);
    transactionRequestService.commit(request);
  }

/*
  @ApiOperation(value = "입/출고 삭제")
  @DeleteMapping("/transaction-requests/{id}")
  @PreAuthorize("hasRole('PROJECT_MANAGER')")
  public void delete(@PathVariable("id") TransactionRequestId id) {
    transactionRequestService.delete(new TransactionRequestRequests.DeleteRequest(id));
  }
  */

  @ApiOperation(value = "제출하지 않은 입/출고 요청을 취소")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  @DeleteMapping(value = "/transaction-requests/cancel-uncommitted")
  public void cancelUncommitted(
    @RequestBody TransactionRequestRequests.CancelUncommittedRequest request) {
    transactionRequestService.cancelUncommitted(request);
  }

  @ApiOperation(value = "입/출고 생성")
  @PutMapping("/transaction-requests/{id}/commit")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  public void commit(@PathVariable("id") TransactionRequestId id,
    @RequestBody TransactionRequestRequests.CommitRequest request) {
    request.setId(id);
    transactionRequestService.commit(request);
  }

  @ApiOperation(value = "입/출고 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/transaction-requests")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  public void create(@RequestBody TransactionRequestRequests.CreateRequest request) {
    transactionRequestService.create(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "입/출고 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  @GetMapping(value = "/transaction-requests/{id}", consumes = MediaType.ALL_VALUE)
  public TransactionRequestData get(@PathVariable("id") TransactionRequestId id) {
    return transactionRequestService.get(id);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "입/출고 검색")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  @GetMapping(value = "/transaction-requests", consumes = MediaType.ALL_VALUE)
  public Page<TransactionRequestView> retrieve(@ModelAttribute TransactionRequestView.Filter filter,
    Pageable pageable) {
    return transactionRequestQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "입/출고 수정")
  @PutMapping("/transaction-requests/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_TRANSACTION_REQUESTER')")
  public void update(@PathVariable("id") TransactionRequestId id,
    @RequestBody TransactionRequestRequests.UpdateRequest request) {
    request.setId(id);
    transactionRequestService.update(request);
  }

}
