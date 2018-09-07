package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.process.PreprocessTypeQuery;
import pico.erp.process.PreprocessTypeRequests;
import pico.erp.process.PreprocessTypeService;
import pico.erp.process.data.PreprocessTypeData;
import pico.erp.process.data.PreprocessTypeId;
import pico.erp.process.data.PreprocessTypeView;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("preprocess-type-controller-v1")
@RequestMapping(value = "/process", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class PreprocessTypeController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private PreprocessTypeService preprocessTypeService;

  @Lazy
  @Autowired
  private PreprocessTypeQuery preprocessTypeQuery;


  @Autowired
  private MessageSource messageSource;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "공정 유형 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/preprocess-type-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asLabels(
    @RequestParam("query") String keyword) {
    return preprocessTypeQuery.asLabels(keyword, labelQueryLimit);
  }

  @ApiOperation(value = "공정 유형 생성")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/preprocess-types")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void create(@RequestBody PreprocessTypeRequests.CreateRequest request) {
    preprocessTypeService.create(request);
  }

  @ApiOperation(value = "공정 유형 삭제")
  @DeleteMapping("/preprocess-types/{id}")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void delete(@PathVariable("id") PreprocessTypeId id) {
    preprocessTypeService.delete(new PreprocessTypeRequests.DeleteRequest(id));
  }

  @ApiOperation(value = "공정 유형 조회")
  @PreAuthorize("hasAnyRole('PROCESS_TYPE_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/preprocess-types/{id}", consumes = MediaType.ALL_VALUE)
  public PreprocessTypeData get(@PathVariable("id") PreprocessTypeId id) {
    return preprocessTypeService.get(id);
  }

  @ApiOperation(value = "공정 유형 검색")
  @PreAuthorize("hasAnyRole('PROCESS_TYPE_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/preprocess-types", consumes = MediaType.ALL_VALUE)
  public Page<PreprocessTypeView> retrieve(
    @ModelAttribute PreprocessTypeView.Filter filter,
    Pageable pageable) {
    return preprocessTypeQuery.retrieve(filter, pageable);
  }

  @ApiOperation(value = "공정 유형 수정")
  @PutMapping("/preprocess-types/{id}")
  @PreAuthorize("hasRole('PROCESS_TYPE_MANAGER')")
  public void update(@PathVariable("id") PreprocessTypeId id,
    @RequestBody PreprocessTypeRequests.UpdateRequest request) {
    request.setId(id);
    preprocessTypeService.update(request);
  }

}
