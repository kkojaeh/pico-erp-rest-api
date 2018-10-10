package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.attachment.AttachmentData;
import pico.erp.attachment.AttachmentId;
import pico.erp.attachment.AttachmentRequests;
import pico.erp.attachment.AttachmentRequests.CreateRequest;
import pico.erp.attachment.AttachmentService;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("attachment-controller-v1")
@RequestMapping(value = "/attachment", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class AttachmentController {

  private final FileTypeMap fileTypeMap = MimetypesFileTypeMap.getDefaultFileTypeMap();

  @Lazy
  @Autowired
  private AttachmentService attachmentService;

  @ApiOperation(value = "첨부 생성")
  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/attachments")
  public AttachmentData create(@RequestBody CreateRequest request) {
    AttachmentData result = attachmentService.create(request);
    return result;
  }

  @ApiOperation(value = "첨부 조회")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/attachments/{id}", consumes = MediaType.ALL_VALUE)
  public AttachmentData get(@PathVariable("id") AttachmentId id) {
    return attachmentService.get(id);
  }

  @CacheControl(maxAge = 60 * 60 * 24 * 30)
  @ApiOperation(value = "아이콘 다운로드(extension)")
  @GetMapping(value = "/icons/{extension:[\\.\\w]+}", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> getIcon(
    @PathVariable(value = "extension") String extension) {

    val image = attachmentService
      .getIcon(fileTypeMap.getContentType(extension));

    return ResponseEntity.ok()
      .contentLength(image.getContentLength())
      .contentType(MediaType.valueOf(image.getContentType()))
      .body(new InputStreamResource(image.getInputStream()));
  }

  @CacheControl(maxAge = 60 * 60 * 24 * 30)
  @ApiOperation(value = "아이콘 다운로드(content-type)")
  @GetMapping(value = "/icons/{contentTypePrefix}/{contentTypePostfix:[\\.\\-\\w]+}", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> getIcon(
    @PathVariable(value = "contentTypePrefix") String contentTypePrefix,
    @PathVariable(value = "contentTypePostfix") String contentTypePostfix) {

    val image = attachmentService
      .getIcon(contentTypePrefix + "/" + contentTypePostfix);

    return ResponseEntity.ok()
      .contentLength(image.getContentLength())
      .contentType(MediaType.valueOf(image.getContentType()))
      .body(new InputStreamResource(image.getInputStream()));
  }

  @ApiOperation(value = "첨부 정리")
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping(value = "/clear")
  public void clear(@RequestBody AttachmentRequests.ClearRequest request) {
    attachmentService.clear(request);
  }

}
