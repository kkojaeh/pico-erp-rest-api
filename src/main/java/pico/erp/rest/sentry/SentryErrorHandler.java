package pico.erp.rest.sentry;

import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.util.ErrorHandler;
import pico.erp.shared.data.Auditor;

@Slf4j
public class SentryErrorHandler implements ErrorHandler {

  private static boolean initialized = false;

  @Autowired
  private AuditorAware<Auditor> auditorAware;

  @Value("${sentry.exception.exclude-patterns}")
  private List<String> excludePatterns;


  @Override
  public void handleError(Throwable t) {
    if (!initialized) {
      Sentry.init();
      initialized = true;
    }
    Throwable cause = t;
    while (cause.getCause() != null && cause.getCause() != cause) {
      cause = cause.getCause();
    }
    boolean excluded = false;
    String className = cause.getClass().getName();
    if (excludePatterns != null && !excludePatterns.isEmpty()) {
      excluded = excludePatterns.stream()
        .anyMatch(pattern -> className.matches(toPattern(pattern)));
    }
    if (!excluded) {
      Auditor auditor = auditorAware.getCurrentAuditor().get();
      if (auditor != null) {
        Sentry.getContext().setUser(
          new UserBuilder()
            .setId(auditor.getId())
            .setUsername(auditor.getName())
            .build()
        );
      }
      Sentry.capture(t);
    }
    log.error(t.getMessage(), t);
  }

  private String toPattern(String pattern) {
    return pattern
      .replaceAll("\\.", "\\\\.")
      .replaceAll("\\*", ".*");
  }

}
