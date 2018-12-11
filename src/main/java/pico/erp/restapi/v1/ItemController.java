package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pico.erp.item.ItemData;
import pico.erp.item.ItemId;
import pico.erp.item.ItemQuery;
import pico.erp.item.ItemRequests;
import pico.erp.item.ItemRequests.ActivateRequest;
import pico.erp.item.ItemRequests.DeactivateRequest;
import pico.erp.item.ItemService;
import pico.erp.item.ItemStatusKind;
import pico.erp.item.ItemTransporter;
import pico.erp.item.ItemTypeKind;
import pico.erp.item.ItemView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.ExtendedLabeledValue;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("item-controller-v1")
@RequestMapping(value = "/item", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ItemController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ItemService itemService;

  @Lazy
  @Autowired
  private ItemQuery itemQuery;

  @Lazy
  @Autowired
  private ItemTransporter itemTransporter;


  @Autowired
  private MessageSource messageSource;

  @ApiOperation(value = "품목 활성화")
  @PutMapping("/items/{id}/activate")
  @PreAuthorize("hasRole('ITEM_MANAGER')")
  public void activate(@PathVariable("id") ItemId id, @RequestBody ActivateRequest request) {
    request.setId(id);
    itemService.activate(request);
  }

  @ApiOperation(value = "품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/items")
  @PreAuthorize("hasRole('ITEM_MANAGER')")
  public ItemData create(@RequestBody ItemRequests.CreateRequest request) {
    return itemService.create(request);
  }

  @ApiOperation(value = "품목 비활성화")
  @PutMapping("/items/{id}/deactivate")
  @PreAuthorize("hasRole('ITEM_MANAGER')")
  public void deactivate(@PathVariable("id") ItemId id,
    @RequestBody DeactivateRequest request) {
    request.setId(id);
    itemService.deactivate(request);
  }


  @ApiOperation(value = "품목 삭제")
  @DeleteMapping("/items/{id}")
  @PreAuthorize("hasRole('ITEM_MANAGER')")
  public void delete(@PathVariable("id") ItemId id) {
    itemService.delete(
      new ItemRequests.DeleteRequest(id)
    );
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 조회")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'ITEM_ACCESSOR')")
  @GetMapping(value = "/items/{id}", consumes = MediaType.ALL_VALUE)
  public ItemData get(@PathVariable("id") ItemId id) {
    return itemService.get(id);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "품목 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/item-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> itemAsLabels(@RequestParam("query") String keyword) {
    return itemQuery.asLabels(keyword, labelQueryLimit);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "품목 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/item-status-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> itemStatusLabels() {
    Locale locale = LocaleContextHolder.getLocale();
    return Stream.of(ItemStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, locale)
        )
      ).collect(Collectors.toList());
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "품목 유형 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/item-type-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> itemTypeLabels() {
    Locale locale = LocaleContextHolder.getLocale();
    return Stream.of(ItemTypeKind.values())
      .map(kind ->
        ExtendedLabeledValue.builder()
          .value(kind.name())
          .label(
            messageSource.getMessage(kind.getNameCode(), null, locale))
          .subLabel(messageSource
            .getMessage(kind.getDescriptionCode(), null, locale))
          .build()
      );
  }

  @ApiOperation(value = "품목 검색")
  @PreAuthorize("hasAnyRole('ITEM_MANAGER', 'ITEM_ACCESSOR')")
  @GetMapping(value = "/items", consumes = MediaType.ALL_VALUE)
  public Page<ItemView> retrieve(@ModelAttribute ItemView.Filter filter,
    Pageable pageable) {
    return itemQuery.retrieve(filter, pageable);
  }


  @ApiOperation(value = "품목 수정")
  @PutMapping("/items/{id}")
  @PreAuthorize("hasRole('ITEM_MANAGER')")
  public void update(@PathVariable("id") ItemId id,
    @RequestBody ItemRequests.UpdateRequest request) {
    request.setId(id);
    itemService.update(request);
  }

  @SneakyThrows
  @ApiOperation(value = "공정 유형 export as xlsx")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  @GetMapping(value = "/xlsx/items", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> exportAs(
    ItemTransporter.ExportRequest request) {
    return SharedController.asResponse(itemTransporter.exportExcel(request));
  }

  @SneakyThrows
  @ApiOperation(value = "공정 유형 import by xlsx")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  @PostMapping(value = "/xlsx/items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public boolean importBy(@RequestPart MultipartFile file,
    ItemTransporter.ImportRequest request) {
    request.setInputStream(file.getInputStream());
    itemTransporter.importExcel(request);
    return true;
  }

}
