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
import pico.erp.warehouse.location.site.SiteData;
import pico.erp.warehouse.location.site.SiteId;
import pico.erp.warehouse.location.site.SiteRequests;
import pico.erp.warehouse.location.site.SiteService;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("warehouse-location-site-controller-v1")
@RequestMapping(value = "/warehouse", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WarehouseSiteController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private SiteService siteService;


  @ApiOperation(value = "창고 사이트 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/location/sites")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void create(@RequestBody SiteRequests.CreateRequest request) {
    siteService.create(request);
  }


  @ApiOperation(value = "창고 사이트 삭제")
  @DeleteMapping("/location/sites/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void delete(@PathVariable("id") SiteId id) {
    siteService.delete(new SiteRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/sites/{id}", consumes = MediaType.ALL_VALUE)
  public SiteData get(@PathVariable("id") SiteId id) {
    return siteService.get(id);
  }

  @ApiOperation(value = "창고 사이트 조회")
  @PreAuthorize("hasAnyRole('WAREHOUSE_MANAGER', 'WAREHOUSE_ACCESSOR')")
  @GetMapping(value = "/location/sites", consumes = MediaType.ALL_VALUE)
  public List<SiteData> getAll() {
    return siteService.getAll();
  }

  @ApiOperation(value = "창고 사이트 수정")
  @PutMapping("/location/sites/{id}")
  @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
  public void update(@PathVariable("id") SiteId id,
    @RequestBody SiteRequests.UpdateRequest request) {
    request.setId(id);
    siteService.update(request);
  }

}
