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
import pico.erp.warehouse.location.bay.WarehouseBayData;
import pico.erp.warehouse.location.bay.WarehouseBayId;
import pico.erp.warehouse.location.bay.WarehouseBayRequests;
import pico.erp.warehouse.location.bay.WarehouseBayService;
import pico.erp.warehouse.location.rack.WarehouseRackId;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("warehouse-location-bay-controller-v1")
@RequestMapping(value = "/warehouse", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WarehouseBayController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private WarehouseBayService warehouseBayService;


  @ApiOperation(value = "창고 사이트 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/location/bays")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void create(@RequestBody WarehouseBayRequests.CreateRequest request) {
    warehouseBayService.create(request);
  }


  @ApiOperation(value = "창고 사이트 삭제")
  @DeleteMapping("/location/bays/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void delete(@PathVariable("id") WarehouseBayId id) {
    warehouseBayService.delete(new WarehouseBayRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/bays/{id}", consumes = MediaType.ALL_VALUE)
  public WarehouseBayData get(@PathVariable("id") WarehouseBayId id) {
    return warehouseBayService.get(id);
  }

  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/racks/{rackId}/bays", consumes = MediaType.ALL_VALUE)
  public List<WarehouseBayData> getAll(@PathVariable("rackId") WarehouseRackId rackId) {
    return warehouseBayService.getAll(rackId);
  }

  @ApiOperation(value = "창고 사이트 수정")
  @PutMapping("/location/bays/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void update(@PathVariable("id") WarehouseBayId id,
    @RequestBody WarehouseBayRequests.UpdateRequest request) {
    request.setId(id);
    warehouseBayService.update(request);
  }

}
