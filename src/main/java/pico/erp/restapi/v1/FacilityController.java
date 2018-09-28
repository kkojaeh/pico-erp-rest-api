package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.facility.FacilityQuery;
import pico.erp.facility.FacilityRequests;
import pico.erp.facility.FacilityService;
import pico.erp.facility.category.data.FacilityCategory;
import pico.erp.facility.category.data.FacilityCategoryId;
import pico.erp.facility.data.FacilityData;
import pico.erp.facility.data.FacilityId;
import pico.erp.facility.data.FacilityView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("facility-controller-v1")
@RequestMapping(value = "/facility", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class FacilityController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private FacilityService facilityService;

  @Lazy
  @Autowired
  private FacilityQuery facilityQuery;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "설비 분류 선택을 위한 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/facility-category-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asCategoryLabels() {
    return facilityQuery.asCategoryLabels();
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "설비 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/facility-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asLabels(@RequestParam("query") String keyword) {
    return facilityQuery.asLabels(keyword, labelQueryLimit);
  }

  @ApiOperation(value = "설비 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/facilities")
  @PreAuthorize("hasRole('FACILITY_MANAGER')")
  public void create(@RequestBody FacilityRequests.CreateRequest request) {
    facilityService.create(request);
  }


  @ApiOperation(value = "설비 삭제")
  @DeleteMapping("/facilities/{id}")
  @PreAuthorize("hasRole('FACILITY_MANAGER')")
  public void delete(@PathVariable("id") FacilityId id) {
    facilityService.delete(new FacilityRequests.DeleteRequest(id));
  }


  @ApiOperation(value = "설비 조회")
  @PreAuthorize("hasAnyRole('FACILITY_MANAGER', 'FACILITY_ACCESSOR')")
  @GetMapping(value = "/facilities/{id}", consumes = MediaType.ALL_VALUE)
  public FacilityData get(@PathVariable("id") FacilityId id) {
    return facilityService.get(id);
  }

  @ApiOperation(value = "설비 분류 조회")
  @PreAuthorize("hasAnyRole('FACILITY_MANAGER', 'FACILITY_ACCESSOR')")
  @GetMapping(value = "/categories/{id}", consumes = MediaType.ALL_VALUE)
  public FacilityCategory get(@PathVariable("id") FacilityCategoryId id) {
    return facilityService.get(id);
  }

  @ApiOperation(value = "설비 검색")
  @PreAuthorize("hasAnyRole('FACILITY_MANAGER', 'FACILITY_ACCESSOR')")
  @GetMapping(value = "/facilities", consumes = MediaType.ALL_VALUE)
  public Page<FacilityView> retrieve(@ModelAttribute FacilityView.Filter filter,
    Pageable pageable) {
    return facilityQuery.retrieve(filter, pageable);
  }


  @ApiOperation(value = "설비 수정")
  @PutMapping("/facilities/{id}")
  @PreAuthorize("hasRole('FACILITY_MANAGER')")
  public void update(@PathVariable("id") FacilityId id,
    @RequestBody FacilityRequests.UpdateRequest request) {
    request.setId(id);
    facilityService.update(request);
  }

}
