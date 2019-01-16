package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
import pico.erp.quotation.QuotationId;
import pico.erp.quotation.addition.QuotationAdditionData;
import pico.erp.quotation.addition.QuotationAdditionId;
import pico.erp.quotation.addition.QuotationAdditionRequests;
import pico.erp.quotation.addition.QuotationAdditionService;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("quotation-addition-controller-v1")
@RequestMapping(value = "/quotation", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class QuotationAdditionController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private QuotationAdditionService quotationAdditionService;


  @ApiOperation(value = "견적 부가 추가")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/quotations/{quotationId}/additions")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public QuotationAdditionData create(@PathVariable("quotationId") QuotationId quotationId,
    @RequestBody QuotationAdditionRequests.CreateRequest request) {
    request.setQuotationId(quotationId);
    return quotationAdditionService.create(request);
  }

  @ApiOperation(value = "견적 품목 삭제")
  @DeleteMapping("/quotations/{quotationId}/additions/{id}")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void delete(@PathVariable("quotationId") QuotationId quotationId,
    @PathVariable("id") QuotationAdditionId id) {
    quotationAdditionService.delete(
      new QuotationAdditionRequests.DeleteRequest(id)
    );
  }


  @ApiOperation(value = "견적 부가 검색")
  @PreAuthorize("hasAnyRole('QUOTATION_MANAGER', 'QUOTATION_ACCESSOR')")
  @GetMapping(value = "/quotations/{quotationId}/additions", consumes = MediaType.ALL_VALUE)
  public List<QuotationAdditionData> getAdditions(
    @PathVariable("quotationId") QuotationId quotationId) {
    return quotationAdditionService.getAll(quotationId);
  }


  @ApiOperation(value = "견적 부가 수정")
  @PutMapping("/quotations/{quotationId}/additions/{id}")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void update(@PathVariable("quotationId") QuotationId quotationId,
    @PathVariable("id") QuotationAdditionId id,
    @RequestBody QuotationAdditionRequests.UpdateRequest request) {
    request.setId(id);
    quotationAdditionService.update(request);
  }

}
