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
import pico.erp.company.contact.CompanyContactData;
import pico.erp.company.contact.CompanyContactId;
import pico.erp.company.contact.CompanyContactQuery;
import pico.erp.company.contact.CompanyContactRequests;
import pico.erp.company.contact.CompanyContactService;
import pico.erp.company.contact.CompanyContactView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("company-contact-controller-v1")
@RequestMapping(value = "/company", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class CompanyContactController {

  @Value("${label.query.limit}")
  long labelQueryLimit;


  @Lazy
  @Autowired
  private CompanyContactQuery companyContactQuery;

  @Lazy
  @Autowired
  private CompanyContactService companyContactService;


  @ApiOperation(value = "회사 연락처 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/contacts")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  public CompanyContactData create(@RequestBody CompanyContactRequests.CreateRequest request) {
    return companyContactService.create(request);
  }


  @ApiOperation(value = "회사 연락처 삭제")
  @DeleteMapping("/contacts/{id}")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  public void delete(@PathVariable("id") CompanyContactId id) {
    companyContactService.delete(new CompanyContactRequests.DeleteRequest(id));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "회사 연락처 조회")
  @PreAuthorize("hasAnyRole('COMPANY_MANAGER', 'COMPANY_ACCESSOR')")
  @GetMapping(value = "/contacts/{id}", consumes = MediaType.ALL_VALUE)
  public CompanyContactData get(@PathVariable("id") CompanyContactId id) {
    return companyContactService.get(id);
  }

  @ApiOperation(value = "회사 연락처 검색")
  @PreAuthorize("hasAnyRole('COMPANY_MANAGER', 'COMPANY_ACCESSOR')")
  @GetMapping(value = "/contacts", consumes = MediaType.ALL_VALUE)
  public Page<CompanyContactView> retrieve(
    @ModelAttribute CompanyContactView.Filter filter,
    Pageable pageable) {
    return companyContactQuery.retrieve(filter, pageable);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "회사 연락처 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/contact-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> retrieveContactLabels(
    @RequestParam("companyId") CompanyId companyId, @RequestParam("query") String keyword) {
    return companyContactQuery.asLabels(companyId, keyword, labelQueryLimit);
  }

  @ApiOperation(value = "지정 회사 연락처 검색")
  @PreAuthorize("hasAnyRole('COMPANY_MANAGER', 'COMPANY_ACCESSOR')")
  @GetMapping(value = "/companies/{companyId}/contacts", consumes = MediaType.ALL_VALUE)
  public List<CompanyContactData> retrieveContracts(
    @PathVariable("companyId") CompanyId companyId) {
    return companyContactService.getAll(companyId);
  }

  @ApiOperation(value = "회사 연락처 수정")
  @PutMapping("/contacts/{id}")
  @PreAuthorize("hasRole('COMPANY_MANAGER')")
  public void update(@PathVariable("id") CompanyContactId id,
    @RequestBody CompanyContactRequests.UpdateRequest request) {
    request.setId(id);
    companyContactService.update(request);
  }

}
