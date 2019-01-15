package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.notify.type.NotifyTypeData;
import pico.erp.notify.type.NotifyTypeId;
import pico.erp.notify.type.NotifyTypeQuery;
import pico.erp.notify.type.NotifyTypeRequests;
import pico.erp.notify.type.NotifyTypeService;
import pico.erp.notify.type.NotifyTypeView;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("notify-type-controller-v1")
@RequestMapping(value = "/notify", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class NotifyTypeController {

  @Lazy
  @Autowired
  private NotifyTypeService notifyTypeService;

  @Lazy
  @Autowired
  private NotifyTypeQuery notifyTypeQuery;

  @ApiOperation(value = "알림 유형 조회")
  @PreAuthorize("hasRole('NOTIFY_MANAGER')")
  @GetMapping(value = "/types/{id}", consumes = MediaType.ALL_VALUE)
  public NotifyTypeData get(@PathVariable("id") NotifyTypeId id) {
    return notifyTypeService.get(id);
  }

  @ApiOperation(value = "알림 유형 검색")
  @PreAuthorize("hasRole('NOTIFY_MANAGER')")
  @GetMapping(value = "/types", consumes = MediaType.ALL_VALUE)
  public Page<NotifyTypeView> retrieve(NotifyTypeView.Filter filter,
    Pageable pageable) {
    return notifyTypeQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "알림 유형 수정")
  @PutMapping("/types/{id}")
  @PreAuthorize("hasRole('NOTIFY_MANAGER')")
  public void update(@PathVariable("id") NotifyTypeId id,
    @RequestBody NotifyTypeRequests.UpdateRequest request) {
    request.setId(id);
    notifyTypeService.update(request);
  }

  @ApiOperation(value = "알림 유형 템플릿 테스트")
  @PreAuthorize("hasRole('NOTIFY_MANAGER')")
  @PostMapping(value = "/types/{id}/test-compile/markdown", consumes = MediaType.ALL_VALUE)
  public String testCompileAsMarkdown(@PathVariable("id") NotifyTypeId id,
    @RequestBody NotifyTypeRequests.TestCompileRequest request) {
    request.setId(id);
    try {
      return notifyTypeService.testCompile(request).asMarkdown();
    } catch (Throwable t) {
      val writer = new StringWriter();
      t.printStackTrace(new PrintWriter(writer));
      return writer.toString();
    }

  }

}
