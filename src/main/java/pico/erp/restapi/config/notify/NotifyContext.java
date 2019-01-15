package pico.erp.restapi.config.notify;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class NotifyContext {

  @Getter
  public final Map data = new HashMap<>();

  @Getter
  @NonNull
  private final Function<String, String> dateFormatter;

  @Getter
  @NonNull
  private final Function<String, String> dateTimeFormatter;

  @Getter
  @NonNull
  private final Function<String, String> phoneNumberFormatter;

  @Getter
  @NonNull
  private final String locationOrigin;

}
