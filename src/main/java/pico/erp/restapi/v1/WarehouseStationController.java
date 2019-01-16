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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.warehouse.location.site.SiteId;
import pico.erp.warehouse.location.station.StationData;
import pico.erp.warehouse.location.station.StationId;
import pico.erp.warehouse.location.station.StationQuery;
import pico.erp.warehouse.location.station.StationRequests;
import pico.erp.warehouse.location.station.StationService;

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
  private StationService stationService;

  @Lazy
  @Autowired
  private StationQuery stationQuery;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "창고지 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/location/station-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asLabels(@RequestParam("query") String keyword) {
    return stationQuery.asLabels(keyword, labelQueryLimit);
  }

  @ApiOperation(value = "창고 사이트 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/location/stations")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void create(@RequestBody StationRequests.CreateRequest request) {
    stationService.create(request);
  }

  @ApiOperation(value = "창고 사이트 삭제")
  @DeleteMapping("/location/stations/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void delete(@PathVariable("id") StationId id) {
    stationService.delete(new StationRequests.DeleteRequest(id));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/stations/{id}", consumes = MediaType.ALL_VALUE)
  public StationData get(@PathVariable("id") StationId id) {
    return stationService.get(id);
  }

  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/sites/{siteId}/stations", consumes = MediaType.ALL_VALUE)
  public List<StationData> getAll(@PathVariable("siteId") SiteId siteId) {
    return stationService.getAll(siteId);
  }

  @ApiOperation(value = "창고 사이트 수정")
  @PutMapping("/location/stations/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void update(@PathVariable("id") StationId id,
    @RequestBody StationRequests.UpdateRequest request) {
    request.setId(id);
    stationService.update(request);
  }

}
