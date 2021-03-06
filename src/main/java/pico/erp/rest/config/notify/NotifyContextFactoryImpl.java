package pico.erp.rest.config.notify;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pico.erp.rest.ClientProperties;

@Component
public class NotifyContextFactoryImpl implements NotifyContextFactory {

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm:ss");

  private static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

  @Autowired
  private ClientProperties clientProperties;

  private static String dateFormatter(String value) {
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    return dateFormatter.format(OffsetDateTime.parse(value));
  }

  private static String dateTimeFormatter(String value) {
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    return dateTimeFormatter.format(OffsetDateTime.parse(value));
  }

  private static Locale getLocale() {
    return LocaleContextHolder.getLocale();
  }

  @SneakyThrows
  private static String phoneNumberFormatter(String value) {
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    PhoneNumber number = phoneNumberUtil.parse(value, getLocale().getCountry());
    return String.format("(+%d) %s", number.getCountryCode(),
      phoneNumberUtil.format(number, PhoneNumberFormat.NATIONAL));
  }

  @Override
  public NotifyContext factory() {
    return NotifyContext.builder()
      .dateFormatter(NotifyContextFactoryImpl::dateFormatter)
      .dateTimeFormatter(NotifyContextFactoryImpl::dateTimeFormatter)
      .phoneNumberFormatter(NotifyContextFactoryImpl::phoneNumberFormatter)
      .locationOrigin(clientProperties.getLocationOrigin())
      .build();
  }
}
