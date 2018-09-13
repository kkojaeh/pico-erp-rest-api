package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.item.data.ItemSpecTypeData;
import pico.erp.item.data.ItemSpecTypeId;
import pico.erp.item.spec.type.ItemSpecTypeQuery;
import pico.erp.item.spec.type.ItemSpecTypeService;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("item-spec-type-controller-v1")
@RequestMapping(value = "/item", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ItemSpecTypeController {

  @Value("${label.query.limit}")
  long labelQueryLimit;


  @Lazy
  @Autowired
  private ItemSpecTypeQuery itemSpecTypeQuery;

  @Lazy
  @Autowired
  private ItemSpecTypeService itemSpecTypeService;

  @Autowired
  private MessageSource messageSource;


  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 스펙 유형 조회")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'ITEM_ACCESSOR', 'BOM_MANAGER', 'BOM_ACCESSOR')")
  @GetMapping(value = "/spec-types/{id:[a-zA-Z_0-9\\.]+}", consumes = MediaType.ALL_VALUE)
  public ItemSpecTypeData getItemSpecType(@PathVariable("id") ItemSpecTypeId id) {
    return itemSpecTypeService.get(id);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 스펙 유형 검색을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/spec-type-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> specTypeAsLabels(
    @RequestParam("query") String keyword) {
    return itemSpecTypeQuery.asLabels(keyword, labelQueryLimit);
  }


}
