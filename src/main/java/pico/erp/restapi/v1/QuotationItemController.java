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
import pico.erp.quotation.data.QuotationId;
import pico.erp.quotation.item.QuotationItemRequests;
import pico.erp.quotation.item.QuotationItemService;
import pico.erp.quotation.item.data.QuotationItemData;
import pico.erp.quotation.item.data.QuotationItemId;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("quotation-item-controller-v1")
@RequestMapping(value = "/quotation", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class QuotationItemController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @Lazy
  @Autowired
  private QuotationItemService quotationItemService;

  @ApiOperation(value = "견적 품목 추가")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/quotations/{quotationId}/items")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void create(@PathVariable("quotationId") QuotationId quotationId,
    @RequestBody QuotationItemRequests.CreateRequest request) {
    request.setQuotationId(quotationId);
    quotationItemService.create(request);
  }

  @ApiOperation(value = "견적 품목 삭제")
  @DeleteMapping("/quotations/{quotationId}/items/{id}")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void delete(@PathVariable("quotationId") QuotationId quotationId,
    @PathVariable("id") QuotationItemId id) {
    quotationItemService.delete(new QuotationItemRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "견적 품목 검색")
  @PreAuthorize("hasAnyRole('QUOTATION_MANAGER', 'QUOTATION_ACCESSOR')")
  @GetMapping(value = "/quotations/{quotationId}/items", consumes = MediaType.ALL_VALUE)
  public List<QuotationItemData> getItems(@PathVariable("quotationId") QuotationId quotationId) {
    return quotationItemService.getAll(quotationId);
  }

  @ApiOperation(value = "견적 품목 수정")
  @PutMapping("/quotations/{quotationId}/items/{id}")
  @PreAuthorize("hasRole('QUOTATION_MANAGER')")
  public void update(@PathVariable("quotationId") QuotationId quotationId,
    @PathVariable("id") QuotationItemId id,
    @RequestBody QuotationItemRequests.UpdateRequest request) {
    request.setId(id);
    quotationItemService.update(request);
  }

}
