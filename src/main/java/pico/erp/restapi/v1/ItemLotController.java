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
import pico.erp.item.lot.ItemLotData;
import pico.erp.item.lot.ItemLotId;
import pico.erp.item.lot.ItemLotRequests;
import pico.erp.item.lot.ItemLotService;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("item-lot-controller-v1")
@RequestMapping(value = "/item", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ItemLotController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ItemLotService itemLotService;

  @Autowired
  private MessageSource messageSource;

  @ApiOperation(value = "품목 LOT 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/lots")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'BOM_MANAGER')")
  public ItemLotData create(@RequestBody ItemLotRequests.CreateRequest request) {
    return itemLotService.create(request);
  }

  @ApiOperation(value = "품목 LOT 삭제")
  @DeleteMapping("/lots/{id}")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'BOM_MANAGER')")
  public void delete(@PathVariable("id") ItemLotId id) {
    itemLotService.delete(
      new ItemLotRequests.DeleteRequest(id)
    );
  }

  @ApiOperation(value = "품목 LOT 만료")
  @DeleteMapping("/lot-expire")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER')")
  public void expire(@RequestBody ItemLotRequests.ExpireRequest request) {
    itemLotService.expire(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 LOT 조회")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'BOM_MANAGER', 'ITEM_ACCESSOR')")
  @GetMapping(value = "/lots/{id}", consumes = MediaType.ALL_VALUE)
  public ItemLotData get(@PathVariable("id") ItemLotId id) {
    return itemLotService.get(id);
  }

  @ApiOperation(value = "품목 LOT 수정")
  @PutMapping("/lots/{id}")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'BOM_MANAGER')")
  public void update(@PathVariable("id") ItemLotId id,
    @RequestBody ItemLotRequests.UpdateRequest request) {
    request.setId(id);
    itemLotService.update(request);
  }

}
