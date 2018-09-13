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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.item.category.ItemCategoryQuery;
import pico.erp.item.category.ItemCategoryRequests;
import pico.erp.item.category.ItemCategoryService;
import pico.erp.item.data.ItemCategoryData;
import pico.erp.item.data.ItemCategoryHierarchyView;
import pico.erp.item.data.ItemCategoryId;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("item-category-controller-v1")
@RequestMapping(value = "/item", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ItemCategoryController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ItemCategoryService itemCategoryService;

  @Lazy
  @Autowired
  private ItemCategoryQuery itemCategoryQuery;

  @Autowired
  private MessageSource messageSource;


  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 분류 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/category-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> categoryAsLabels(
    @RequestParam("query") String keyword) {
    return itemCategoryQuery.asLabels(keyword, labelQueryLimit);
  }

  @ApiOperation(value = "품목 분류 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/categories")
  @PreAuthorize("hasRole('ITEM_MANAGER')")
  public ItemCategoryData create(@RequestBody ItemCategoryRequests.CreateRequest request) {

    return itemCategoryService.create(request);
  }


  @ApiOperation(value = "품목 분류 삭제")
  @DeleteMapping("/categories/{id}")
  @PreAuthorize("hasRole('ITEM_MANAGER')")
  public void delete(@PathVariable("id") ItemCategoryId id) {
    itemCategoryService.delete(
      new ItemCategoryRequests.DeleteRequest(id)
    );
  }


  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 분류 조회")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'ITEM_ACCESSOR')")
  @GetMapping(value = "/categories/{id}", consumes = MediaType.ALL_VALUE)
  public ItemCategoryData get(@PathVariable("id") ItemCategoryId id) {
    return itemCategoryService.get(id);
  }

  @ApiOperation(value = "품목 분류 검색")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'ITEM_ACCESSOR')")
  @GetMapping(value = "/categories", consumes = MediaType.ALL_VALUE)
  public List<ItemCategoryHierarchyView> retrieve() {
    return itemCategoryQuery.findAllAsHierarchy();
  }


  @ApiOperation(value = "품목 분류 수정")
  @PutMapping("/categories/{id}")
  @PreAuthorize("hasRole('ITEM_MANAGER')")
  public void update(@PathVariable("id") ItemCategoryId id,
    @RequestBody ItemCategoryRequests.UpdateRequest request) {
    request.setId(id);
    itemCategoryService.update(request);
  }


}
