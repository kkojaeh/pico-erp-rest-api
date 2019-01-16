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
import pico.erp.warehouse.location.site.SiteId;
import pico.erp.warehouse.location.zone.ZoneData;
import pico.erp.warehouse.location.zone.ZoneId;
import pico.erp.warehouse.location.zone.ZoneRequests;
import pico.erp.warehouse.location.zone.ZoneService;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("warehouse-location-zone-controller-v1")
@RequestMapping(value = "/warehouse", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WarehouseZoneController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ZoneService zoneService;


  @ApiOperation(value = "창고 사이트 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/location/zones")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public ZoneData create(@RequestBody ZoneRequests.CreateRequest request) {
    return zoneService.create(request);
  }


  @ApiOperation(value = "창고 사이트 삭제")
  @DeleteMapping("/location/zones/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void delete(@PathVariable("id") ZoneId id) {
    zoneService.delete(new ZoneRequests.DeleteRequest(id));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/zones/{id}", consumes = MediaType.ALL_VALUE)
  public ZoneData get(@PathVariable("id") ZoneId id) {
    return zoneService.get(id);
  }

  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/sites/{siteId}/zones", consumes = MediaType.ALL_VALUE)
  public List<ZoneData> getAll(@PathVariable("siteId") SiteId siteId) {
    return zoneService.getAll(siteId);
  }

  @ApiOperation(value = "창고 사이트 수정")
  @PutMapping("/location/zones/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void update(@PathVariable("id") ZoneId id,
    @RequestBody ZoneRequests.UpdateRequest request) {
    request.setId(id);
    zoneService.update(request);
  }

}
