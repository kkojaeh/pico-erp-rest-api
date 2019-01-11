package pico.erp.restapi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ErrorHandler;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pico.erp.shared.data.AuthorizedUser;

@ControllerAdvice
public class ControllerConfig {

  @Autowired
  private BasicErrorController basicErrorController;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private ErrorHandler errorHandler;

  @ResponseBody
  @ResponseStatus(code = HttpStatus.FORBIDDEN)
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> accessDeniedException(HttpServletRequest request,
    AccessDeniedException e, @AuthenticationPrincipal AuthorizedUser userDetails) {
    request.setAttribute("javax.servlet.error.status_code", HttpStatus.FORBIDDEN.value());
    ResponseEntity<Map<String, Object>> response = basicErrorController.error(request);
    response.getBody().put("authorized", userDetails != null);
    return response;
  }

  @ResponseBody
  @ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> constraintViolationException(
    HttpServletRequest request, ConstraintViolationException e) {
    ResponseEntity<Map<String, Object>> response = basicErrorController.error(request);

    Map<Object, BeanPropertyBindingResult> bindings = new HashMap<>();

    List<ObjectError> errors = new LinkedList<>();
    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      Object leafBean = violation.getLeafBean();
      if (!bindings.containsKey(leafBean)) {
        bindings.put(leafBean,
          new BeanPropertyBindingResult(leafBean, leafBean.getClass().getSimpleName()));
      }
      BeanPropertyBindingResult binding = bindings.get(leafBean);
      String leafName = StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
        .map(Node::getName)
        .reduce((r, n) -> n)
        .get();
      ElementKind leafKind = StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
        .map(Node::getKind)
        .reduce((r, n) -> n)
        .get();
      String code = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
      if (leafKind == ElementKind.PROPERTY) {
        binding.rejectValue(leafName, code, violation.getMessage());
      } else {
        binding.reject(code, violation.getMessage());
      }
    }
    bindings.keySet().stream()
      .map(bindings::get)
      .forEach(b -> errors.addAll(b.getAllErrors()));
    response.getBody().put("errors", errors);
    return response;
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> exception(HttpServletRequest request, Exception e) {
    errorHandler.handleError(e);
    Throwable cause = e;
    while (cause.getCause() != null && cause.getCause() != cause) {
      cause = cause.getCause();
    }
    request.setAttribute("javax.servlet.error.exception", cause);
    ResponseStatus responseStatus = AnnotatedElementUtils
      .findMergedAnnotation(cause.getClass(), ResponseStatus.class);
    if (responseStatus != null) {
      request.setAttribute("javax.servlet.error.status_code", responseStatus.code().value());
      String reason = responseStatus.reason();
      if (StringUtils.hasLength(reason)) {
        String resolvedReason = (this.messageSource != null ?
          this.messageSource.getMessage(reason, null, reason, LocaleContextHolder.getLocale()) :
          reason);
        request.setAttribute("javax.servlet.error.message", resolvedReason);
      }
    }
    ResponseEntity<Map<String, Object>> response = basicErrorController.error(request);
    return response;
  }


}
