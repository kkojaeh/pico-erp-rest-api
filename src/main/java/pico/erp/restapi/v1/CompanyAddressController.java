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
import pico.erp.company.CompanyId;
import pico.erp.company.address.CompanyAddressData;
import pico.erp.company.address.CompanyAddressId;
import pico.erp.company.address.CompanyAddressQuery;
import pico.erp.company.address.CompanyAddressRequests;
import pico.erp.company.address.CompanyAddressService;
import pico.erp.company.address.CompanyAddressView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("company-address-controller-v1")
@RequestMapping(value = "/company", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class CompanyAddressController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private CompanyAddressQuery companyAddressQuery;

  @Lazy
  @Autowired
  private CompanyAddressService companyAddressService;


  @ApiOperation(value = "회사 주소지 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/addresses")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  public void create(@RequestBody CompanyAddressRequests.CreateRequest request) {
    companyAddressService.create(request);
  }


  @ApiOperation(value = "회사 주소지 삭제")
  @DeleteMapping("/addresses/{id}")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  public void delete(@PathVariable("id") CompanyAddressId id) {
    companyAddressService.delete(new CompanyAddressRequests.DeleteRequest(id));
  }


  @ApiOperation(value = "회사 주소지 조회")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  @GetMapping(value = "/addresses/{id}", consumes = MediaType.ALL_VALUE)
  public CompanyAddressData get(@PathVariable("id") CompanyAddressId id) {
    return companyAddressService.get(id);
  }

  @ApiOperation(value = "회사 주소지 검색")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  @GetMapping(value = "/addresses", consumes = MediaType.ALL_VALUE)
  public Page<CompanyAddressView> retrieve(
    @ModelAttribute CompanyAddressView.Filter filter,
    Pageable pageable) {
    return companyAddressQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "회사 연락처 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/address-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> retrieveAddressLabels(
    @RequestParam("companyId") CompanyId companyId, @RequestParam("query") String keyword) {
    return companyAddressQuery.asLabels(companyId, keyword, labelQueryLimit);
  }

  @ApiOperation(value = "지정 회사 주소지 검색")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  @GetMapping(value = "/companies/{companyId}/addresses", consumes = MediaType.ALL_VALUE)
  public List<CompanyAddressData> retrieveAddresses(
    @PathVariable("companyId") CompanyId companyId) {
    return companyAddressService.getAll(companyId);
  }

  @ApiOperation(value = "회사 주소지 수정")
  @PutMapping("/addresses/{id}")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  public void update(@PathVariable("id") CompanyAddressId id,
    @RequestBody CompanyAddressRequests.UpdateRequest request) {
    request.setId(id);
    companyAddressService.update(request);
  }

}
