package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.delivery.DeliveryId;
import pico.erp.delivery.result.DeliveryResultData;
import pico.erp.delivery.result.DeliveryResultService;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("delivery-result-controller-v1")
@RequestMapping(value = "/delivery", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class DeliveryResultController {

  @ComponentAutowired
  private DeliveryResultService deliveryResultService;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "전달 결과 조회")
  @PreAuthorize("hasAnyRole('DELIVERY_ACCESSOR', 'DELIVERY_CHARGER', 'DELIVERY_MANAGER')")
  @GetMapping(value = "/deliveries/{id}/results", consumes = MediaType.ALL_VALUE)
  public List<DeliveryResultData> getAll(@PathVariable("id") DeliveryId id) {
    return deliveryResultService.getAll(id);
  }

}
