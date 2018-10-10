package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import pico.erp.item.spec.ItemSpecData;
import pico.erp.item.spec.ItemSpecId;
import pico.erp.item.spec.ItemSpecRequests;
import pico.erp.item.spec.ItemSpecService;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("item-spec-controller-v1")
@RequestMapping(value = "/item", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ItemSpecController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ItemSpecService itemSpecService;

  @Autowired
  private MessageSource messageSource;

  @ApiOperation(value = "품목 스펙 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/specs")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'BOM_MANAGER')")
  public ItemSpecData create(@RequestBody ItemSpecRequests.CreateRequest request) {
    return itemSpecService.create(request);
  }

  @ApiOperation(value = "품목 스펙 삭제")
  @DeleteMapping("/specs/{id}")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'BOM_MANAGER')")
  public void delete(@PathVariable("id") ItemSpecId id) {
    itemSpecService.delete(
      new ItemSpecRequests.DeleteRequest(id)
    );
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 스펙 조회")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'BOM_MANAGER', 'ITEM_ACCESSOR')")
  @GetMapping(value = "/specs/{id}", consumes = MediaType.ALL_VALUE)
  public ItemSpecData get(@PathVariable("id") ItemSpecId id) {
    return itemSpecService.get(id);
  }

  @ApiOperation(value = "품목 스펙 수정")
  @PutMapping("/specs/{id}")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'BOM_MANAGER')")
  public void update(@PathVariable("id") ItemSpecId id,
    @RequestBody ItemSpecRequests.UpdateRequest request) {
    request.setId(id);
    itemSpecService.update(request);
  }

}
