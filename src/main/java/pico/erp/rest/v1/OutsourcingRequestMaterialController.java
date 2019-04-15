package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
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
import pico.erp.outsourcing.request.OutsourcingRequestId;
import pico.erp.outsourcing.request.material.OutsourcingRequestMaterialData;
import pico.erp.outsourcing.request.material.OutsourcingRequestMaterialId;
import pico.erp.outsourcing.request.material.OutsourcingRequestMaterialRequests;
import pico.erp.outsourcing.request.material.OutsourcingRequestMaterialService;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("outsourcing-request-material-controller-v1")
@RequestMapping(value = "/outsourcing-request", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class OutsourcingRequestMaterialController {

  @ComponentAutowired
  private OutsourcingRequestMaterialService outsourcingRequestMaterialService;

  @ApiOperation(value = "발주 품목 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/requests/{requestId}/materials")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUESTER', 'OUTSOURCING_REQUEST_ACCEPTER', 'OUTSOURCING_REQUEST_MANAGER')")
  public OutsourcingRequestMaterialData create(
    @PathVariable("requestId") OutsourcingRequestId requestId,
    @RequestBody OutsourcingRequestMaterialRequests.CreateRequest request) {
    request.setRequestId(requestId);
    return outsourcingRequestMaterialService.create(request);
  }

  @ApiOperation(value = "발주 품목 삭제")
  @DeleteMapping("/requests/{requestId}/materials/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUESTER', 'OUTSOURCING_REQUEST_ACCEPTER', 'OUTSOURCING_REQUEST_MANAGER')")
  public void delete(@PathVariable("requestId") OutsourcingRequestId requestId,
    @PathVariable("id") OutsourcingRequestMaterialId id) {
    outsourcingRequestMaterialService
      .delete(new OutsourcingRequestMaterialRequests.DeleteRequest(id));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "발주 품목 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUESTER', 'OUTSOURCING_REQUEST_ACCEPTER', 'OUTSOURCING_REQUEST_MANAGER')")
  @GetMapping(value = "/materials/{id}", consumes = MediaType.ALL_VALUE)
  public OutsourcingRequestMaterialData get(@PathVariable("id") OutsourcingRequestMaterialId id) {
    return outsourcingRequestMaterialService.get(id);
  }

  @ApiOperation(value = "발주 품목 조회")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUESTER', 'OUTSOURCING_REQUEST_ACCEPTER', 'OUTSOURCING_REQUEST_MANAGER')")
  @GetMapping(value = "/requests/{requestId}/materials", consumes = MediaType.ALL_VALUE)
  public List<OutsourcingRequestMaterialData> getAll(
    @PathVariable("requestId") OutsourcingRequestId requestId) {
    return outsourcingRequestMaterialService.getAll(requestId);
  }

  @ApiOperation(value = "발주 품목 수정")
  @PutMapping("/requests/{requestId}/materials/{id}")
  @PreAuthorize("hasAnyRole('OUTSOURCING_REQUEST_CHARGER', 'OUTSOURCING_REQUEST_MANAGER')")
  public void update(@PathVariable("requestId") OutsourcingRequestId requestId,
    @PathVariable("id") OutsourcingRequestMaterialId id,
    @RequestBody OutsourcingRequestMaterialRequests.UpdateRequest request) {
    request.setId(id);
    outsourcingRequestMaterialService.update(request);
  }

}
