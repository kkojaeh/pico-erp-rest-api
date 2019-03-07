package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.production.execution.ProductionExecutionQuery;
import pico.erp.production.mediator.ProductionPlanDetailMediatorData;
import pico.erp.production.mediator.ProductionPlanMediatorService;
import pico.erp.production.plan.detail.ProductionPlanDetailId;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("production-mediator-controller-v1")
@RequestMapping(value = "/production-mediator", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProductionMediatorController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ProductionPlanMediatorService productionPlanMediatorService;

  @Lazy
  @Autowired
  private ProductionExecutionQuery productionExecutionQuery;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "생산 계획 상세 중계자 조회")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/plan-details/{id}", consumes = MediaType.ALL_VALUE)
  public ProductionPlanDetailMediatorData get(@PathVariable("id") ProductionPlanDetailId id) {
    return productionPlanMediatorService.get(id);
  }

}
