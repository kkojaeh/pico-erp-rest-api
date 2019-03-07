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
import pico.erp.production.execution.ProductionExecutionData;
import pico.erp.production.execution.ProductionExecutionId;
import pico.erp.production.execution.ProductionExecutionQuery;
import pico.erp.production.execution.ProductionExecutionRequests;
import pico.erp.production.execution.ProductionExecutionService;
import pico.erp.production.execution.ProductionExecutionView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.user.UserId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("production-execution-controller-v1")
@RequestMapping(value = "/production-execution", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProductionExecutionController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ProductionExecutionService productionExecutionService;

  @Lazy
  @Autowired
  private ProductionExecutionQuery productionExecutionQuery;

  @ApiOperation(value = "구매 요청 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/executions")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUESTER', 'OUTSOURCING_REQUEST_MANAGER')")
  public ProductionExecutionData create(
    @RequestBody ProductionExecutionRequests.CreateRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setExecutorId(UserId.from(userDetails.getUsername()));
    return productionExecutionService.create(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "송장 조회")
  @PreAuthorize("hasAnyRole('PRODUCTION_EXECUTOR', 'PRODUCTION_EXECUTE_MANAGER')")
  @GetMapping(value = "/executions/{id}", consumes = MediaType.ALL_VALUE)
  public ProductionExecutionData get(@PathVariable("id") ProductionExecutionId id) {
    return productionExecutionService.get(id);
  }

  @ApiOperation(value = "송장 수령")
  @PutMapping("/executions/{id}")
  @PreAuthorize("hasAnyRole('PRODUCTION_EXECUTOR', 'PRODUCTION_EXECUTE_MANAGER')")
  public void receive(@PathVariable("id") ProductionExecutionId id,
    @RequestBody ProductionExecutionRequests.UpdateRequest request) {
    request.setId(id);
    productionExecutionService.update(request);
  }

  @ApiOperation(value = "송장 검색")
  @PreAuthorize("hasAnyRole('PRODUCTION_EXECUTOR', 'PRODUCTION_EXECUTE_MANAGER')")
  @GetMapping(value = "/executions", consumes = MediaType.ALL_VALUE)
  public Page<ProductionExecutionView> retrieve(ProductionExecutionView.Filter filter,
    Pageable pageable, @AuthenticationPrincipal AuthorizedUser userDetails) {
    return productionExecutionQuery.retrieve(filter, pageable);
  }

}
