package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.process.preparation.type.ProcessPreparationTypeData;
import pico.erp.process.preparation.type.ProcessPreparationTypeId;
import pico.erp.process.preparation.type.ProcessPreparationTypeQuery;
import pico.erp.process.preparation.type.ProcessPreparationTypeService;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("preparation-type-controller-v1")
@RequestMapping(value = "/process", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProcessPreparationTypeController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Lazy
  @Autowired
  private ProcessPreparationTypeService preparationTypeService;

  @Lazy
  @Autowired
  private ProcessPreparationTypeQuery preparationTypeQuery;

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "공정 유형 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/preparation-type-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> asLabels(
    @RequestParam("query") String keyword) {
    return preparationTypeQuery.asLabels(keyword, labelQueryLimit);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "공정 유형 조회")
  @PreAuthorize("hasAnyRole('PROCESS_TYPE_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/preparation-types/{id}", consumes = MediaType.ALL_VALUE)
  public ProcessPreparationTypeData get(@PathVariable("id") ProcessPreparationTypeId id) {
    return preparationTypeService.get(id);
  }

  @ApiOperation(value = "전체 공정 유형 조회")
  @PreAuthorize("hasAnyRole('PROCESS_TYPE_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/preparation-types", consumes = MediaType.ALL_VALUE)
  public List<ProcessPreparationTypeData> getAll() {
    return preparationTypeService.getAll();
  }


}
