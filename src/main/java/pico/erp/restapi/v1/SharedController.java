package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.ExtendedLabeledValue;
import pico.erp.shared.data.ContentInputStream;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.shared.data.UnitKind;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("shared-controller-v1")
@RequestMapping(value = "/shared", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class SharedController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  private MessageSource messageSource;

  @SneakyThrows
  public static ResponseEntity<InputStreamResource> asResponse(ContentInputStream inputStream) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.valueOf(inputStream.getContentType()));
    headers.setContentLength(inputStream.getContentLength());
    headers.set(HttpHeaders.CONTENT_DISPOSITION,
      String.format(
        "attachment; filename=\"%s\";",
        URLEncoder.encode(inputStream.getName(), "UTF-8").replaceAll("\\+", " ")
      )
    );
    return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "단위 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/unit-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> itemUnitLabels() {
    Locale locale = LocaleContextHolder.getLocale();
    return Stream.of(UnitKind.values())
      .map(kind ->
        ExtendedLabeledValue.builder()
          .value(kind.name())
          .label(
            messageSource.getMessage(kind.getNameCode(), null, locale))
          .build()
      );
  }

}
