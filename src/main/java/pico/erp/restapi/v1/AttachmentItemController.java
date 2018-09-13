package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URLEncoder;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pico.erp.attachment.AttachmentService;
import pico.erp.attachment.data.AttachmentId;
import pico.erp.attachment.item.AttachmentItemRequests;
import pico.erp.attachment.item.AttachmentItemService;
import pico.erp.attachment.item.data.AttachmentItemData;
import pico.erp.attachment.item.data.AttachmentItemId;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("attachment-item-controller-v1")
@RequestMapping(value = "/attachment", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class AttachmentItemController {

  @Lazy
  @Autowired
  private AttachmentService attachmentService;

  @Lazy
  @Autowired
  private AttachmentItemService attachmentItemService;

  @SneakyThrows
  @ApiOperation(value = "첨부 파일 다운로드")
  @GetMapping(value = "/attachments/{attachmentId}/items/{id}", consumes = MediaType.ALL_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<InputStreamResource> download(
    @PathVariable("attachmentId") AttachmentId attachmentId,
    @PathVariable("id") AttachmentItemId id) {
    val headers = new HttpHeaders();
    if (attachmentService.isUriSupported()) {
      val uri = attachmentItemService.access(
        new AttachmentItemRequests.UriAccessRequest(id)
      );
      headers.set("Location", uri.toString());
      return new ResponseEntity<>(headers, HttpStatus.FOUND);
    } else {
      val item = attachmentItemService.get(id);
      val inputStream = attachmentItemService.access(
        new AttachmentItemRequests.DirectAccessRequest(id)
      );
      headers.setContentType(MediaType.valueOf(item.getContentType()));
      headers.setContentLength(item.getContentLength());
      headers.set(HttpHeaders.CONTENT_DISPOSITION,
        String.format(
          "attachment; filename=\"%s\";",
          URLEncoder.encode(item.getName(), "UTF-8").replaceAll("\\+", " ")
        )
      );
      return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
    }
  }

  @ApiOperation(value = "첨부 파일 조회")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/attachments/{attachmentId}/items", consumes = MediaType.ALL_VALUE)
  public List<AttachmentItemData> getItems(@PathVariable("attachmentId") AttachmentId id) {
    return attachmentItemService.getAll(id);
  }

  @CacheControl(maxAge = 60 * 60 * 24 * 30)
  @SneakyThrows
  @ApiOperation(value = "첨부 파일 썸네일")
  @GetMapping(value = "/thumbnails/{attachmentId}/items/{id}", consumes = MediaType.ALL_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<InputStreamResource> getThumbnail(
    @PathVariable("attachmentId") AttachmentId attachmentId,
    @PathVariable("id") AttachmentItemId id,
    @RequestParam(value = "width", defaultValue = "${attachment.thumbnail.default-width}") int width,
    @RequestParam(value = "height", defaultValue = "${attachment.thumbnail.default-height}") int height) {
    val image = attachmentItemService.getThumbnail(
      AttachmentItemRequests.GetThumbnailRequest.builder()
        .id(id)
        .width(width)
        .height(height)
        .build()
    );
    return ResponseEntity.ok()
      .contentLength(image.getContentLength())
      .contentType(MediaType.valueOf(image.getContentType()))
      .body(new InputStreamResource(image.getInputStream()));

  }

  @ApiOperation(value = "첨부 파일 삭제")
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping(value = "/attachments/{attachmentId}/items/{id}")
  public void removeItem(@PathVariable("attachmentId") AttachmentId attachmentId,
    @PathVariable("id") AttachmentItemId id) {
    attachmentItemService.delete(
      AttachmentItemRequests.DeleteRequest.builder()
        .id(id)
        .force(false)
        .build()
    );
  }

  @SneakyThrows
  @ApiOperation(value = "첨부 파일 생성")
  @PreAuthorize("isAuthenticated()")
  @PostMapping(value = "/attachments/{attachmentId}/items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public AttachmentItemData upload(@PathVariable("attachmentId") AttachmentId attachmentId,
    @RequestPart MultipartFile file, @RequestParam("name") String name) {
    return attachmentItemService.create(
      AttachmentItemRequests.CreateRequest.builder()
        .attachmentId(attachmentId)
        .name(name)
        .contentLength(file.getSize())
        .contentType(file.getContentType())
        .inputStream(file.getInputStream())
        .build()
    );
  }

}
