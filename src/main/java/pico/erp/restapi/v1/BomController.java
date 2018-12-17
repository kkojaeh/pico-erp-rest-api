package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
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
import pico.erp.bom.BomData;
import pico.erp.bom.BomId;
import pico.erp.bom.BomQuery;
import pico.erp.bom.BomRequests.DeleteRequest;
import pico.erp.bom.BomRequests.DetermineRequest;
import pico.erp.bom.BomRequests.DraftRequest;
import pico.erp.bom.BomRevisionView;
import pico.erp.bom.BomService;
import pico.erp.bom.BomStatusKind;
import pico.erp.item.ItemId;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.restapi.web.CachePolicy;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("bom-controller-v1")
@RequestMapping(value = "/bom", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class BomController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private BomService bomService;

  @Lazy
  @Autowired
  private BomQuery bomQuery;

  @Autowired
  private MessageSource messageSource;

  @ApiOperation(value = "BOM 삭제")
  @DeleteMapping("/boms/{id}")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public void delete(@PathVariable("id") BomId id) {
    bomService.delete(new DeleteRequest(id));
  }

  @ApiOperation(value = "BOM 확정")
  @PutMapping("/boms/{id}/determine")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public void determine(@PathVariable("id") BomId id,
    @RequestBody DetermineRequest request) {
    request.setId(id);
    bomService.determine(request);
  }

  @ApiOperation(value = "BOM 생성 - 품목 아이디를 이용")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/boms")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public BomData draft(@RequestBody DraftRequest request) {
    return bomService.draft(request);
  }

  @ApiOperation(value = "BOM 품목에 대한 revision 목록 검색")
  @PreAuthorize("hasAnyRole('BOM_MANAGER', 'BOM_ACCESSOR')")
  @GetMapping(value = "/revisions", consumes = MediaType.ALL_VALUE)
  public List<BomRevisionView> findRevisions(@RequestParam("itemId") ItemId itemId) {
    return bomQuery.findRevisions(itemId);
  }

  @ApiOperation(value = "BOM 조회")
  @PreAuthorize("hasAnyRole('BOM_MANAGER', 'BOM_ACCESSOR')")
  @GetMapping(value = "/boms/{id}", consumes = MediaType.ALL_VALUE)
  public BomData get(@PathVariable("id") BomId id) {
    return bomService.get(id);
  }

  @ApiOperation(value = "BOM 조회", notes = "revision 이 0 이하이면 최근 버전으로 조회")
  @PreAuthorize("hasAnyRole('BOM_MANAGER', 'BOM_ACCESSOR')")
  @GetMapping(value = "/items/{itemId}/{revision}", consumes = MediaType.ALL_VALUE)
  public BomData get(@PathVariable("itemId") ItemId itemId,
    @PathVariable("revision") int revision) {
    if (revision > 0) {
      return bomService.get(itemId, revision);
    } else {
      return bomService.get(itemId);
    }
  }

  @CacheControl(maxAge = 3600, policy = CachePolicy.PRIVATE)
  @ApiOperation(value = "BOM 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/bom-status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> bomStatusLabels() {
    Locale locale = LocaleContextHolder.getLocale();
    return Stream.of(BomStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, locale)
        )
      );
  }

}
