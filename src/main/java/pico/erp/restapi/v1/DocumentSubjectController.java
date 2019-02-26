package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URLEncoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.document.subject.DocumentSubjectData;
import pico.erp.document.subject.DocumentSubjectId;
import pico.erp.document.subject.DocumentSubjectQuery;
import pico.erp.document.subject.DocumentSubjectRequests;
import pico.erp.document.subject.DocumentSubjectService;
import pico.erp.document.subject.DocumentSubjectView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("document-subject-controller-v1")
@RequestMapping(value = "/e-document", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class DocumentSubjectController {

  @Lazy
  @Autowired
  private DocumentSubjectService documentSubjectService;

  @Lazy
  @Autowired
  private DocumentSubjectQuery documentSubjectQuery;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "문서 유형 조회")
  @PreAuthorize("hasRole('DOCUMENT_MANAGER')")
  @GetMapping(value = "/subjects/{id}", consumes = MediaType.ALL_VALUE)
  public DocumentSubjectData get(@PathVariable("id") DocumentSubjectId id) {
    return documentSubjectService.get(id);
  }

  @ApiOperation(value = "문서 유형 검색")
  @PreAuthorize("hasRole('DOCUMENT_MANAGER')")
  @GetMapping(value = "/subjects", consumes = MediaType.ALL_VALUE)
  public Page<DocumentSubjectView> retrieve(DocumentSubjectView.Filter filter,
    Pageable pageable) {
    return documentSubjectQuery.retrieve(filter, pageable);
  }

  @SneakyThrows
  @ApiOperation(value = "문서 유형 테스트")
  @PreAuthorize("hasRole('DOCUMENT_MANAGER')")
  @PostMapping(value = "/subjects/{id}/test", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> test(@PathVariable("id") DocumentSubjectId id,
    @RequestBody DocumentSubjectRequests.TestRequest request) {
    request.setId(id);
    val inputStream = documentSubjectService.test(request);
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

  @ApiOperation(value = "문서 유형 수정")
  @PutMapping("/subjects/{id}")
  @PreAuthorize("hasRole('DOCUMENT_MANAGER')")
  public void update(@PathVariable("id") DocumentSubjectId id,
    @RequestBody DocumentSubjectRequests.UpdateRequest request) {
    request.setId(id);
    documentSubjectService.update(request);
  }

}
