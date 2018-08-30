package pico.erp.restapiserver.v1;

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
import pico.erp.company.CompanyQuery;
import pico.erp.company.CompanyRequests.CreateRequest;
import pico.erp.company.CompanyRequests.DeleteRequest;
import pico.erp.company.CompanyRequests.UpdateRequest;
import pico.erp.company.CompanyService;
import pico.erp.company.data.CompanyData;
import pico.erp.company.data.CompanyId;
import pico.erp.company.data.CompanyView;
import pico.erp.company.data.RegistrationNumber;
import pico.erp.restapiserver.Versions;
import pico.erp.restapiserver.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("company-controller-v1")
@RequestMapping(value = "/company", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class CompanyController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private CompanyService companyService;

  @Lazy
  @Autowired
  private CompanyQuery companyQuery;

  @ApiOperation(value = "회사 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/companies")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  public void create(@RequestBody CreateRequest request) {
    companyService.create(request);
  }

  @ApiOperation(value = "회사 삭제")
  @DeleteMapping("/companies/{id}")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  public void delete(@PathVariable("id") CompanyId id) {
    companyService.delete(new DeleteRequest(id));
  }

  @ApiOperation(value = "회사 조회")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  @GetMapping(value = "/companies/{id}", consumes = MediaType.ALL_VALUE)
  public CompanyData get(@PathVariable("id") CompanyId id) {
    return companyService.get(id);
  }

  @ApiOperation(value = "회사 조회(등록번호)")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  @GetMapping(value = "/registration-numbers/{number}", consumes = MediaType.ALL_VALUE)
  public CompanyData get(@PathVariable("number") RegistrationNumber number) {
    return companyService.get(number);
  }

  @ApiOperation(value = "회사 검색")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  @GetMapping(value = "/companies", consumes = MediaType.ALL_VALUE)
  public Page<CompanyView> retrieve(@ModelAttribute CompanyView.Filter filter,
    Pageable pageable) {
    return companyQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "회사 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/company-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> retrieveCompanyLabels(
    @RequestParam("query") String keyword) {
    return companyQuery.asLabels(keyword, labelQueryLimit);
  }

  @ApiOperation(value = "회사 수정")
  @PutMapping("/companies/{id}")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  public void update(@PathVariable("id") CompanyId id,
    @RequestBody UpdateRequest request) {
    request.setId(id);
    companyService.update(request);
  }

}
