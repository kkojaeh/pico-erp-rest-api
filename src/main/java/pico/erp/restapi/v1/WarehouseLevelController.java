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
import pico.erp.warehouse.location.bay.BayId;
import pico.erp.warehouse.location.level.LevelData;
import pico.erp.warehouse.location.level.LevelId;
import pico.erp.warehouse.location.level.LevelRequests;
import pico.erp.warehouse.location.level.LevelService;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("warehouse-location-level-controller-v1")
@RequestMapping(value = "/warehouse", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WarehouseLevelController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private LevelService levelService;


  @ApiOperation(value = "창고 층 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/location/levels")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void create(@RequestBody LevelRequests.CreateRequest request) {
    levelService.create(request);
  }


  @ApiOperation(value = "창고 층 삭제")
  @DeleteMapping("/location/levels/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void delete(@PathVariable("id") LevelId id) {
    levelService.delete(new LevelRequests.DeleteRequest(id));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "창고 층 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/levels/{id}", consumes = MediaType.ALL_VALUE)
  public LevelData get(@PathVariable("id") LevelId id) {
    return levelService.get(id);
  }

  @ApiOperation(value = "창고 층 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/bays/{bayId}/levels", consumes = MediaType.ALL_VALUE)
  public List<LevelData> getAll(@PathVariable("bayId") BayId bayId) {
    return levelService.getAll(bayId);
  }

  @ApiOperation(value = "창고 층 수정")
  @PutMapping("/location/levels/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void update(@PathVariable("id") LevelId id,
    @RequestBody LevelRequests.UpdateRequest request) {
    request.setId(id);
    levelService.update(request);
  }

}
