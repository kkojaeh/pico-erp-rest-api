package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.notify.subject.type.NotifySubjectTypeData;
import pico.erp.notify.subject.type.NotifySubjectTypeId;
import pico.erp.notify.subject.type.NotifySubjectTypeService;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("notify-subject-type-controller-v1")
@RequestMapping(value = "/notify", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class NotifySubjectTypeController {

  @ComponentAutowired
  private NotifySubjectTypeService notifySubjectTypeService;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "알림 주제 유형 조회")
  @PreAuthorize("hasRole('NOTIFY_MANAGER')")
  @GetMapping(value = "/subject-types/{id}", consumes = MediaType.ALL_VALUE)
  public NotifySubjectTypeData get(@PathVariable("id") NotifySubjectTypeId id) {
    return notifySubjectTypeService.get(id);
  }

  @ApiOperation(value = "알림 주제 유형 검색")
  @PreAuthorize("hasRole('NOTIFY_MANAGER')")
  @GetMapping(value = "/subject-types", consumes = MediaType.ALL_VALUE)
  public List<NotifySubjectTypeData> getAll() {
    return notifySubjectTypeService.getAll();
  }

}
