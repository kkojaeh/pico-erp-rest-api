package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.delivery.subject.DeliverySubjectData;
import pico.erp.delivery.subject.DeliverySubjectId;
import pico.erp.delivery.subject.DeliverySubjectQuery;
import pico.erp.delivery.subject.DeliverySubjectRequests;
import pico.erp.delivery.subject.DeliverySubjectService;
import pico.erp.delivery.subject.DeliverySubjectView;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("delivery-subject-controller-v1")
@RequestMapping(value = "/delivery", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class DeliverySubjectController {

  @ComponentAutowired
  private DeliverySubjectService deliverySubjectService;

  @ComponentAutowired
  private DeliverySubjectQuery deliverySubjectQuery;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "전달 유형 조회")
  @PreAuthorize("hasRole('DELIVERY_MANAGER')")
  @GetMapping(value = "/subjects/{id}", consumes = MediaType.ALL_VALUE)
  public DeliverySubjectData get(@PathVariable("id") DeliverySubjectId id) {
    return deliverySubjectService.get(id);
  }

  @ApiOperation(value = "전달 유형 검색")
  @PreAuthorize("hasRole('DELIVERY_MANAGER')")
  @GetMapping(value = "/subjects", consumes = MediaType.ALL_VALUE)
  public Page<DeliverySubjectView> retrieve(DeliverySubjectView.Filter filter,
    Pageable pageable) {
    return deliverySubjectQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "전달 유형 수정")
  @PutMapping("/subjects/{id}")
  @PreAuthorize("hasRole('DELIVERY_MANAGER')")
  public void update(@PathVariable("id") DeliverySubjectId id,
    @RequestBody DeliverySubjectRequests.UpdateRequest request) {
    request.setId(id);
    deliverySubjectService.update(request);
  }

}
