package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.fax.FaxRequests;
import pico.erp.fax.FaxService;
import pico.erp.rest.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("fax-controller-v1")
@RequestMapping(value = "/fax", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class FaxController {

  @ComponentAutowired
  private FaxService faxService;

  @SneakyThrows
  @ApiOperation(value = "FAX 재전송 처리")
  @PreAuthorize("hasRole('FAX_MANAGER')")
  @PutMapping(value = "/revalidate", consumes = MediaType.ALL_VALUE)
  public void revalidate() {
    faxService.revalidate(
      FaxRequests.RevalidateRequest.builder()
        .build()
    );
  }


}
