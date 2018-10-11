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
import pico.erp.warehouse.location.site.WarehouseSiteId;
import pico.erp.warehouse.location.zone.WarehouseZoneData;
import pico.erp.warehouse.location.zone.WarehouseZoneId;
import pico.erp.warehouse.location.zone.WarehouseZoneRequests;
import pico.erp.warehouse.location.zone.WarehouseZoneService;

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
  private WarehouseZoneService warehouseZoneService;


  @ApiOperation(value = "창고 사이트 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/location/zones")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void create(@RequestBody WarehouseZoneRequests.CreateRequest request) {
    warehouseZoneService.create(request);
  }


  @ApiOperation(value = "창고 사이트 삭제")
  @DeleteMapping("/location/zones/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void delete(@PathVariable("id") WarehouseZoneId id) {
    warehouseZoneService.delete(new WarehouseZoneRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/zones/{id}", consumes = MediaType.ALL_VALUE)
  public WarehouseZoneData get(@PathVariable("id") WarehouseZoneId id) {
    return warehouseZoneService.get(id);
  }

  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/sites/{siteId}/zones", consumes = MediaType.ALL_VALUE)
  public List<WarehouseZoneData> getAll(@PathVariable("siteId") WarehouseSiteId siteId) {
    return warehouseZoneService.getAll(siteId);
  }

  @ApiOperation(value = "창고 사이트 수정")
  @PutMapping("/location/zones/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void update(@PathVariable("id") WarehouseZoneId id,
    @RequestBody WarehouseZoneRequests.UpdateRequest request) {
    request.setId(id);
    warehouseZoneService.update(request);
  }

}
