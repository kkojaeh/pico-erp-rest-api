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
import pico.erp.restapi.web.CacheControl;
import pico.erp.warehouse.location.rack.RackData;
import pico.erp.warehouse.location.rack.RackId;
import pico.erp.warehouse.location.rack.RackRequests;
import pico.erp.warehouse.location.rack.RackService;
import pico.erp.warehouse.location.zone.ZoneId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("warehouse-location-rack-controller-v1")
@RequestMapping(value = "/warehouse", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WarehouseRackController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private RackService rackService;


  @ApiOperation(value = "창고 사이트 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/location/racks")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public RackData create(@RequestBody RackRequests.CreateRequest request) {
    return rackService.create(request);
  }


  @ApiOperation(value = "창고 사이트 삭제")
  @DeleteMapping("/location/racks/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void delete(@PathVariable("id") RackId id) {
    rackService.delete(new RackRequests.DeleteRequest(id));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/racks/{id}", consumes = MediaType.ALL_VALUE)
  public RackData get(@PathVariable("id") RackId id) {
    return rackService.get(id);
  }

  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/zones/{zoneId}/racks", consumes = MediaType.ALL_VALUE)
  public List<RackData> getAll(@PathVariable("zoneId") ZoneId zoneId) {
    return rackService.getAll(zoneId);
  }

  @ApiOperation(value = "창고 사이트 수정")
  @PutMapping("/location/racks/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void update(@PathVariable("id") RackId id,
    @RequestBody RackRequests.UpdateRequest request) {
    request.setId(id);
    rackService.update(request);
  }

}
