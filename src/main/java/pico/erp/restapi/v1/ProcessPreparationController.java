package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.process.ProcessId;
import pico.erp.process.preparation.ProcessPreparationData;
import pico.erp.process.preparation.ProcessPreparationId;
import pico.erp.process.preparation.ProcessPreparationRequests;
import pico.erp.process.preparation.ProcessPreparationService;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("preprocess-controller-v1")
@RequestMapping(value = "/process", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProcessPreparationController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ProcessPreparationService preparationService;


  @Autowired
  private MessageSource messageSource;

  @ApiOperation(value = "공정 유형 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/preparations")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public ProcessPreparationData create(
    @RequestBody ProcessPreparationRequests.CreateRequest request) {
    return preparationService.create(request);
  }

  @ApiOperation(value = "공정 유형 삭제")
  @DeleteMapping("/preparations/{id}")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void delete(@PathVariable("id") ProcessPreparationId id) {
    preparationService.delete(new ProcessPreparationRequests.DeleteRequest(id));
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "공정 유형 조회")
  @PreAuthorize("hasAnyRole('PROCESS_TYPE_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/preparations/{id}", consumes = MediaType.ALL_VALUE)
  public ProcessPreparationData get(@PathVariable("id") ProcessPreparationId id) {
    return preparationService.get(id);
  }

  @ApiOperation(value = "공정 유형 검색")
  @PreAuthorize("hasAnyRole('PROCESS_TYPE_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/processes/{processId}/preparations", consumes = MediaType.ALL_VALUE)
  public List<ProcessPreparationData> retrieve(@PathVariable("processId") ProcessId processId) {
    return preparationService.getAll(processId);
  }

  @ApiOperation(value = "공정 유형 수정")
  @PutMapping("/preparations/{id}")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void update(@PathVariable("id") ProcessPreparationId id,
    @RequestBody ProcessPreparationRequests.UpdateRequest request) {
    request.setId(id);
    preparationService.update(request);
  }

}
