package pico.erp.restapi.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pico.erp.restapi.Versions;
import pico.erp.restapi.web.CacheControl;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.warehouse.transaction.TransactionQuantityCorrectionPolicyKind;
import pico.erp.warehouse.transaction.TransactionTypeKind;
import pico.erp.warehouse.transaction.request.TransactionRequestStatusKind;

@Api(produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@RestController("warehouse-controller-v1")
@RequestMapping(value = "/warehouse", produces = Versions.V1_JSON, consumes = Versions.V1_JSON)
@CrossOrigin
@Slf4j
public class WarehouseController {

  @Value("${label.query.limit}")
  long labelQueryLimit;

  @Autowired
  MessageSource messageSource;

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "입/출고 유형 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/transaction-quantity-correction-policy-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> quantityCorrectionPolicyLabels() {
    return Stream.of(TransactionQuantityCorrectionPolicyKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "입/출고 유형 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/transaction-type-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> statusLabels() {
    return Stream.of(TransactionTypeKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

  @CacheControl(maxAge = 3600)
  @ApiOperation(value = "입/출고 요청 상태 목록")
  @PreAuthorize("permitAll")
  @GetMapping(value = "/transaction-request-status-labels", consumes = MediaType.ALL_VALUE)
  public Stream<? extends LabeledValuable> transactionRequestStatusLabels() {
    return Stream.of(TransactionRequestStatusKind.values())
      .map(kind ->
        new LabeledValue(
          kind.name(),
          messageSource.getMessage(kind.getNameCode(), null, LocaleContextHolder.getLocale())
        )
      );
  }

}
