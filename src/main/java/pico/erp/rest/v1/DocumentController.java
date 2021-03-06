package pico.erp.rest.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URLEncoder;
import kkojaeh.spring.boot.component.ComponentAutowired;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.document.DocumentData;
import pico.erp.document.DocumentId;
import pico.erp.document.DocumentService;
import pico.erp.rest.Versions;
import pico.erp.rest.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("document-controller-v1")
@RequestMapping(value = "/e-document", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class DocumentController {

  @ComponentAutowired
  private DocumentService documentService;

  @SneakyThrows
  @ApiOperation(value = "문서 다운로드")
  @PreAuthorize("hasAnyRole('DOCUMENT_ACCESSOR', 'DOCUMENT_MANAGER')")
  @GetMapping(value = "/documents/{id}/download", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> download(@PathVariable("id") DocumentId id) {
    val inputStream = documentService.load(id);
    val headers = new HttpHeaders();
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

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "문서 조회")
  @PreAuthorize("hasAnyRole('DOCUMENT_ACCESSOR')")
  @GetMapping(value = "/documents/{id}", consumes = MediaType.ALL_VALUE)
  public DocumentData get(@PathVariable("id") DocumentId id) {
    return documentService.get(id);
  }

}
