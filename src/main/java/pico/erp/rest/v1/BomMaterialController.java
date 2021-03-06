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
import pico.erp.bom.BomData;
import pico.erp.bom.BomId;
import pico.erp.bom.material.BomMaterialRequests;
import pico.erp.bom.material.BomMaterialService;
import pico.erp.rest.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("bom-material-controller-v1")
@RequestMapping(value = "/bom", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class BomMaterialController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @ComponentAutowired
  private BomMaterialService bomMaterialService;

  @Autowired
  private MessageSource messageSource;

  @ApiOperation(value = "BOM 자재 수정")
  @PutMapping("/boms/{bomId}/materials/{materialId}/order")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public void changeOrder(@PathVariable("bomId") BomId bomId,
    @PathVariable("materialId") BomId materialId,
    @RequestBody BomMaterialRequests.ChangeOrderRequest request) {
    request.setBomId(bomId);
    request.setMaterialId(materialId);
    bomMaterialService.changeOrder(request);
  }

  @ApiOperation(value = "BOM 자재 추가")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/boms/{bomId}/materials")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public BomData create(@PathVariable("bomId") BomId bomId,
    @RequestBody BomMaterialRequests.CreateRequest request) {
    request.setBomId(bomId);
    return bomMaterialService.create(request);
  }

  @ApiOperation(value = "BOM 자재 삭제")
  @DeleteMapping("/boms/{bomId}/materials/{materialId}")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public void delete(@PathVariable("bomId") BomId bomId,
    @PathVariable("materialId") BomId materialId) {
    bomMaterialService.delete(
      new BomMaterialRequests.DeleteRequest(bomId, materialId)
    );
  }

  @ApiOperation(value = "BOM 자재 조회")
  @GetMapping("/boms/{id}/materials")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public List<BomData> getMaterials(@PathVariable("id") BomId id) {
    return bomMaterialService.getAll(id);
  }

  @ApiOperation(value = "BOM 자재 수정")
  @PutMapping("/boms/{bomId}/materials/{materialId}")
  @PreAuthorize("hasRole('BOM_MANAGER')")
  public void update(@PathVariable("bomId") BomId bomId,
    @PathVariable("materialId") BomId materialId,
    @RequestBody BomMaterialRequests.UpdateRequest request) {
    request.setBomId(bomId);
    request.setMaterialId(materialId);
    bomMaterialService.update(request);
  }

}
