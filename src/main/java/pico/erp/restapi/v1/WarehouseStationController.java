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
import pico.erp.warehouse.location.station.WarehouseStationData;
import pico.erp.warehouse.location.station.WarehouseStationId;
import pico.erp.warehouse.location.station.WarehouseStationRequests;
import pico.erp.warehouse.location.station.WarehouseStationService;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("warehouse-location-station-controller-v1")
@RequestMapping(value = "/warehouse", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WarehouseStationController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private WarehouseStationService warehouseStationService;


  @ApiOperation(value = "창고 사이트 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/location/stations")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void create(@RequestBody WarehouseStationRequests.CreateRequest request) {
    warehouseStationService.create(request);
  }


  @ApiOperation(value = "창고 사이트 삭제")
  @DeleteMapping("/location/stations/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void delete(@PathVariable("id") WarehouseStationId id) {
    warehouseStationService.delete(new WarehouseStationRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/stations/{id}", consumes = MediaType.ALL_VALUE)
  public WarehouseStationData get(@PathVariable("id") WarehouseStationId id) {
    return warehouseStationService.get(id);
  }

  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/sites/{siteId}/stations", consumes = MediaType.ALL_VALUE)
  public List<WarehouseStationData> getAll(@PathVariable("siteId") WarehouseSiteId siteId) {
    return warehouseStationService.getAll(siteId);
  }

  @ApiOperation(value = "창고 사이트 수정")
  @PutMapping("/location/stations/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void update(@PathVariable("id") WarehouseStationId id,
    @RequestBody WarehouseStationRequests.UpdateRequest request) {
    request.setId(id);
    warehouseStationService.update(request);
  }

}
