package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.delivery.DeliveryData;
import pico.erp.delivery.DeliveryId;
import pico.erp.delivery.DeliveryRequests;
import pico.erp.delivery.DeliveryService;
import pico.erp.delivery.result.DeliveryResultData;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.AuthorizedUser;
import pico.erp.user.UserId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("delivery-controller-v1")
@RequestMapping(value = "/delivery", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class DeliveryController {

  @Lazy
  @Autowired
  private DeliveryService deliveryService;

  @SneakyThrows
  @ApiOperation(value = "전달 처리")
  @PreAuthorize("hasRole('DELIVERY_CHARGER')")
  @PostMapping(value = "/deliveries/{id}/deliver", consumes = MediaType.ALL_VALUE)
  public DeliveryResultData deliver(@PathVariable("id") DeliveryId id,
    @RequestBody DeliveryRequests.DeliverRequest request,
    @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setId(id);
    request.setRequesterId(UserId.from(userDetails.getUsername()));
    return deliveryService.deliver(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "전달 조회")
  @PreAuthorize("hasRole('DELIVERY_ACCESSOR')")
  @GetMapping(value = "/deliveries/{id}", consumes = MediaType.ALL_VALUE)
  public DeliveryData get(@PathVariable("id") DeliveryId id) {
    return deliveryService.get(id);
  }

}
