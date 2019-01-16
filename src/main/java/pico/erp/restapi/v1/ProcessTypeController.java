package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pico.erp.process.preparation.type.ProcessPreparationTypeId;
import pico.erp.process.type.ProcessTypeData;
import pico.erp.process.type.ProcessTypeId;
import pico.erp.process.type.ProcessTypeQuery;
import pico.erp.process.type.ProcessTypeRequests;
import pico.erp.process.type.ProcessTypeService;
import pico.erp.process.type.ProcessTypeTransporter;
import pico.erp.process.type.ProcessTypeView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("process-type-controller-v1")
@RequestMapping(value = "/process", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProcessTypeController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ProcessTypeService processTypeService;

  @Lazy
  @Autowired
  private ProcessTypeQuery processTypeQuery;


  @Autowired
  private MessageSource messageSource;

  @Lazy
  @Autowired
  private ProcessTypeTransporter processTypeTransporter;

  @ApiOperation(value = "공정 유형 사전공정 유형 추가")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/process-types/{id}/preparation-types")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void add(
    @PathVariable("id") ProcessTypeId id,
    @RequestBody ProcessTypeRequests.AddPreprocessTypeRequest request) {
    request.setId(id);
    processTypeService.add(request);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "공정 유형 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/process-type-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asLabels(
    @RequestParam("query") String keyword) {
    return processTypeQuery.asLabels(keyword, labelQueryLimit);
  }

  @ApiOperation(value = "공정 유형 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/process-types")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void create(@RequestBody ProcessTypeRequests.CreateRequest request) {
    processTypeService.create(request);
  }

  @ApiOperation(value = "공정 유형 삭제")
  @DeleteMapping("/process-types/{id}")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void delete(@PathVariable("id") ProcessTypeId id) {
    processTypeService.delete(new ProcessTypeRequests.DeleteRequest(id));
  }

  @SneakyThrows
  @ApiOperation(value = "export as xlsx")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  @GetMapping(value = "/xlsx/process-types", consumes = MediaType.ALL_VALUE)
  public ResponseEntity<InputStreamResource> exportAs(
    ProcessTypeTransporter.ExportRequest request) {
    return SharedController.asResponse(processTypeTransporter.exportExcel(request));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "공정 유형 조회")
  @PreAuthorize("hasAnyRole('PROCESS_TYPE_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/process-types/{id}", consumes = MediaType.ALL_VALUE)
  public ProcessTypeData get(@PathVariable("id") ProcessTypeId id) {
    return processTypeService.get(id);
  }

  @SneakyThrows
  @ApiOperation(value = "import by xlsx")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  @PostMapping(value = "/xlsx/process-types", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public boolean importBy(@RequestPart MultipartFile file,
    ProcessTypeTransporter.ImportRequest request) {
    request.setInputStream(file.getInputStream());
    processTypeTransporter.importExcel(request);
    return true;
  }

  @ApiOperation(value = "공정 유형 사전공정 유형 제거")
  @DeleteMapping("/process-types/{id}/preparation-types/{preparationTypeId}")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void remove(
    @PathVariable("id") ProcessTypeId id,
    @PathVariable("preparationTypeId") ProcessPreparationTypeId preparationTypeId,
    @RequestBody ProcessTypeRequests.RemovePreprocessTypeRequest request) {
    request.setId(id);
    request.setPreparationTypeId(preparationTypeId);
    processTypeService.remove(request);
  }

  @ApiOperation(value = "공정 유형 검색")
  @PreAuthorize("hasAnyRole('PROCESS_TYPE_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/process-types", consumes = MediaType.ALL_VALUE)
  public Page<ProcessTypeView> retrieve(@ModelAttribute ProcessTypeView.Filter filter,
    Pageable pageable) {
    return processTypeQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "공정 유형 수정")
  @PutMapping("/process-types/{id}")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void update(@PathVariable("id") ProcessTypeId id,
    @RequestBody ProcessTypeRequests.UpdateRequest request) {
    request.setId(id);
    processTypeService.update(request);
  }

}
