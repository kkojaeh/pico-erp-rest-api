package pico.erp.restapiserver.v1;

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
import pico.erp.process.ProcessInfoTypeQuery;
import pico.erp.process.ProcessInfoTypeService;
import pico.erp.process.data.ProcessInfoTypeData;
import pico.erp.process.data.ProcessInfoTypeId;
import pico.erp.restapiserver.Versions;
import pico.erp.restapiserver.web.CacheControl;
import pico.erp.shared.data.LabeledValuable;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("process-info-type-controller-v1")
@RequestMapping(value = "/process", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class ProcessInfoTypeController {

  @Value("${label.query.limit}")
  long labelQueryLimit;


  @Lazy
  @Autowired
  private ProcessInfoTypeService processInfoTypeService;

  @Lazy
  @Autowired
  private ProcessInfoTypeQuery processInfoTypeQuery;

  @ApiOperation(value = "공정 정보 유형 조회")
  @PreAuthorize("hasAnyRole('PROCESS_TYPE_MANAGER', 'PROCESS_ACCESSOR')")
  @GetMapping(value = "/process-info-types/{id:[a-zA-Z_0-9\\.]+}", consumes = MediaType.ALL_VALUE)
  public ProcessInfoTypeData getProcessInfoType(@PathVariable("id") ProcessInfoTypeId id) {
    return processInfoTypeService.get(id);
  }

  @CacheControl(maxAge = 300)
  @ApiOperation(value = "공정 정보 유형 선택을 위한 키워드 검색")
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/process-info-type-query-labels", consumes = MediaType.ALL_VALUE)
  public List<? extends LabeledValuable> processInfoTypeAsLabels(
    @RequestParam("query") String keyword) {
    return processInfoTypeQuery.asLabels(keyword, labelQueryLimit);
  }

}
