package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
import pico.erp.quotation.QuotationId;
import pico.erp.quotation.item.addition.QuotationItemAdditionData;
import pico.erp.quotation.item.addition.QuotationItemAdditionId;
import pico.erp.quotation.item.addition.QuotationItemAdditionRequests;
import pico.erp.quotation.item.addition.QuotationItemAdditionService;
import pico.erp.rest.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("quotation-item-addition-controller-v1")
@RequestMapping(value = "/quotation", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class QuotationItemAdditionController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @ComponentAutowired
  private QuotationItemAdditionService quotationItemAdditionService;


  @ApiOperation(value = "견적 품목 부가 추가")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/quotations/{quotationId}/item-additions")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public QuotationItemAdditionData create(@PathVariable("quotationId") QuotationId quotationId,
    @RequestBody QuotationItemAdditionRequests.CreateRequest request) {
    request.setQuotationId(quotationId);
    return quotationItemAdditionService.create(request);
  }

  @ApiOperation(value = "견적 품목 부가 삭제")
  @DeleteMapping("/quotations/{quotationId}/item-additions/{id}")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void delete(@PathVariable("quotationId") QuotationId quotationId,
    @PathVariable("id") QuotationItemAdditionId id) {
    quotationItemAdditionService.delete(
      new QuotationItemAdditionRequests.DeleteRequest(id)
    );
  }

  @ApiOperation(value = "견적 품목 부가 검색")
  @PreAuthorize("hasAnyRole('QUOTATION_MANAGER', 'QUOTATION_ACCESSOR')")
  @GetMapping(value = "/quotations/{quotationId}/item-additions", consumes = MediaType.ALL_VALUE)
  public List<QuotationItemAdditionData> getItemAdditions(
    @PathVariable("quotationId") QuotationId quotationId) {
    return quotationItemAdditionService.getAll(quotationId);
  }

  @ApiOperation(value = "견적 품목 부가 수정")
  @PutMapping("/quotations/{quotationId}/item-additions/{id}")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void update(@PathVariable("quotationId") QuotationId quotationId,
    @PathVariable("id") QuotationItemAdditionId id,
    @RequestBody QuotationItemAdditionRequests.UpdateRequest request) {
    request.setId(id);
    quotationItemAdditionService.update(request);
  }


}
