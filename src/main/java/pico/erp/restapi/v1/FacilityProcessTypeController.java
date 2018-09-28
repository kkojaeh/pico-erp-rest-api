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
import pico.erp.facility.data.FacilityId;
import pico.erp.facility.process.type.FacilityProcessTypeRequests;
import pico.erp.facility.process.type.FacilityProcessTypeService;
import pico.erp.facility.process.type.data.FacilityProcessTypeData;
import pico.erp.facility.process.type.data.FacilityProcessTypeId;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("facility-process-type-controller-v1")
@RequestMapping(value = "/facility", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class FacilityProcessTypeController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private FacilityProcessTypeService facilityProcessTypeService;

  @ApiOperation(value = "설비 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/process-types")
  @PreAuthorize("hasRole('FACILITY_MANAGER')")
  public void create(@RequestBody FacilityProcessTypeRequests.CreateRequest request) {
    facilityProcessTypeService.create(request);
  }


  @ApiOperation(value = "설비 삭제")
  @DeleteMapping("/process-types/{id}")
  @PreAuthorize("hasRole('FACILITY_MANAGER')")
  public void delete(@PathVariable("id") FacilityProcessTypeId id) {
    facilityProcessTypeService.delete(new FacilityProcessTypeRequests.DeleteRequest(id));
  }


  @ApiOperation(value = "설비 조회")
  @PreAuthorize("hasAnyRole('FACILITY_MANAGER', 'FACILITY_ACCESSOR')")
  @GetMapping(value = "/process-types/{id}", consumes = MediaType.ALL_VALUE)
  public FacilityProcessTypeData get(@PathVariable("id") FacilityProcessTypeId id) {
    return facilityProcessTypeService.get(id);
  }


  @ApiOperation(value = "설비 검색")
  @PreAuthorize("hasAnyRole('FACILITY_MANAGER', 'FACILITY_ACCESSOR')")
  @GetMapping(value = "/facilities/{facilityId}/process-types", consumes = MediaType.ALL_VALUE)
  public List<FacilityProcessTypeData> retrieve(
    @PathVariable("facilityId") FacilityId facilityId) {
    return facilityProcessTypeService.getAll(facilityId);
  }


  @ApiOperation(value = "설비 수정")
  @PutMapping("/process-types/{id}")
  @PreAuthorize("hasRole('FACILITY_MANAGER')")
  public void update(@PathVariable("id") FacilityProcessTypeId id,
    @RequestBody FacilityProcessTypeRequests.UpdateRequest request) {
    request.setId(id);
    facilityProcessTypeService.update(request);
  }

}
