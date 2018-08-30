package pico.erp.restapiserver.v1;

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
import pico.erp.process.PreprocessRequests;
import pico.erp.process.PreprocessService;
import pico.erp.process.data.PreprocessData;
import pico.erp.process.data.PreprocessId;
import pico.erp.process.data.ProcessId;
import pico.erp.restapiserver.Versions;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("preprocess-controller-v1")
@RequestMapping(value = "/process", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class PreprocessController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private PreprocessService preprocessService;


  @Autowired
  private MessageSource messageSource;

  @ApiOperation(value = "공정 유형 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/preprocesses")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void create(@RequestBody PreprocessRequests.CreateRequest request) {
    preprocessService.create(request);
  }

  @ApiOperation(value = "공정 유형 삭제")
  @DeleteMapping("/preprocesses/{id}")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void delete(@PathVariable("id") PreprocessId id) {
    preprocessService.delete(new PreprocessRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "공정 유형 조회")
  @PreAuthorize("hasAnyRole('PROCESS_TYPE_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/preprocesses/{id}", consumes = MediaType.ALL_VALUE)
  public PreprocessData get(@PathVariable("id") PreprocessId id) {
    return preprocessService.get(id);
  }

  @ApiOperation(value = "공정 유형 검색")
  @PreAuthorize("hasAnyRole('PROCESS_TYPE_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/processes/{processId}/preprocesses", consumes = MediaType.ALL_VALUE)
  public List<PreprocessData> retrieve(@PathVariable("processId") ProcessId processId) {
    return preprocessService.getAll(processId);
  }

  @ApiOperation(value = "공정 유형 수정")
  @PutMapping("/preprocesses/{id}")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void update(@PathVariable("id") PreprocessId id,
    @RequestBody PreprocessRequests.UpdateRequest request) {
    request.setId(id);
    preprocessService.update(request);
  }

}
