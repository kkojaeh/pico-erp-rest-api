package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.notify.sender.NotifySenderData;
import pico.erp.notify.sender.NotifySenderId;
import pico.erp.notify.sender.NotifySenderService;
import pico.erp.restapi.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("notify-sender-controller-v1")
@RequestMapping(value = "/notify", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class NotifySenderController {

  @Lazy
  @Autowired
  private NotifySenderService notifySenderService;

  @ApiOperation(value = "알림 주제 유형 조회")
  @PreAuthorize("hasRole('NOTIFY_MANAGER')")
  @GetMapping(value = "/senders/{id}", consumes = MediaType.ALL_VALUE)
  public NotifySenderData get(@PathVariable("id") NotifySenderId id) {
    return notifySenderService.get(id);
  }

  @ApiOperation(value = "알림 주제 유형 검색")
  @PreAuthorize("hasRole('NOTIFY_MANAGER')")
  @GetMapping(value = "/senders", consumes = MediaType.ALL_VALUE)
  public List<NotifySenderData> getAll() {
    return notifySenderService.getAll();
  }

}
