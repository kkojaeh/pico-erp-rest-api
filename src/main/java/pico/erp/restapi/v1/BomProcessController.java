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
import pico.erp.bom.BomId;
import pico.erp.bom.process.BomProcessData;
import pico.erp.bom.process.BomProcessId;
import pico.erp.bom.process.BomProcessRequests;
import pico.erp.bom.process.BomProcessService;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("bom-process-controller-v1")
@RequestMapping(value = "/bom", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class BomProcessController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private BomProcessService bomProcessService;

  @Autowired
  private MessageSource messageSource;

  @ApiOperation(value = "BOM 자재 수정")
  @PutMapping("/boms/{bomId}/processes/{id}/order")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public void changeOrder(@PathVariable("bomId") BomId bomId,
    @PathVariable("id") BomProcessId id,
    @RequestBody BomProcessRequests.ChangeOrderRequest request) {
    request.setId(id);
    bomProcessService.changeOrder(request);
  }

  @ApiOperation(value = "BOM 자재 추가")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/boms/{bomId}/processes")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public BomProcessData create(@PathVariable("bomId") BomId bomId,
    @RequestBody BomProcessRequests.CreateRequest request) {
    request.setBomId(bomId);
    return bomProcessService.create(request);
  }

  @ApiOperation(value = "BOM 자재 삭제")
  @DeleteMapping("/boms/{bomId}/processes/{id}")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public void delete(@PathVariable("bomId") BomId bomId,
    @PathVariable("id") BomProcessId id) {
    bomProcessService.delete(
      new BomProcessRequests.DeleteRequest(id)
    );
  }

  @ApiOperation(value = "BOM 자재 조회")
  @GetMapping("/boms/{id}/processes")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public List<BomProcessData> getMaterials(@PathVariable("id") BomId id) {
    return bomProcessService.getAll(id);
  }

  @ApiOperation(value = "BOM 자재 수정")
  @PutMapping("/boms/{bomId}/processes/{id}")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public void update(@PathVariable("bomId") BomId bomId,
    @PathVariable("id") BomProcessId id,
    @RequestBody BomProcessRequests.UpdateRequest request) {
    request.setId(id);
    bomProcessService.update(request);
  }

}
