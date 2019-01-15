package pico.erp.restapi.config.notify;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
@Builder
public class NotifyContext implements Map {

  @Delegate(types = Map.class)
  private final Map map = new HashMap<>();

  @NonNull
  private final Function<String, String> dateFormatter;

  @NonNull
  private final Function<String, String> dateTimeFormatter;

  @NonNull
  private final Function<String, String> phoneNumberFormatter;

  @NonNull
  private final Supplier<String> locationOrigin;


}
