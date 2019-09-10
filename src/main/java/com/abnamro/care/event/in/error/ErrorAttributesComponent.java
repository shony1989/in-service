package com.abnamro.care.event.in.error;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.MDC;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.WebRequest;

import com.abnamro.care.event.in.error.dto.ErrorAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@RequiredArgsConstructor
@Primary
@Slf4j
public class ErrorAttributesComponent extends DefaultErrorAttributes {

  private static final String STATUS_PROPERTY_NAME = "status";
  private static final String TRACE_ID_PROPERTY_NAME = "X-B3-TraceId";
  private static final String DEFAULT_ERROR_CODE = "ERR_DEFAULT_ERROR";
  private static final String ERRORS_PROPERTY_NAME = "errors";
  private static final String ERROR_PROPERTY_NAME = "error";
  private static final String MESSAGE_PROPERTY_NAME = "message";

  private final MessageSource messageSource;

  @Override
  public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
    Map<String, Object> rawErrorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);

    Map<String, Object> errorAttributes = Optional.ofNullable(rawErrorAttributes).orElse(Map.of());

    String message = ". ".concat(errorAttributes.getOrDefault(MESSAGE_PROPERTY_NAME, "").toString());

    ErrorAttributes commonAttribute = ErrorAttributes.builder().code(DEFAULT_ERROR_CODE)
        .message(errorAttributes.getOrDefault(ERROR_PROPERTY_NAME, "").toString()
            .concat(message))
        .status(Integer.valueOf(errorAttributes.get(STATUS_PROPERTY_NAME).toString()))
        .traceId(MDC.get(TRACE_ID_PROPERTY_NAME)).build();

    Object wrappedObjectError = errorAttributes.get(ERRORS_PROPERTY_NAME);

    errorAttributes.clear();

    Optional.ofNullable(wrappedObjectError).ifPresentOrElse(woe -> {
      @SuppressWarnings("unchecked")
      List<ObjectError> objectErrors = (List<ObjectError>) woe;
      List<ErrorAttributes> errors =
          ErrorAttributesMapper.mapErrorAttributesFieldError(objectErrors, commonAttribute, messageSource);
      errorAttributes.put(ERRORS_PROPERTY_NAME, errors);
    }, () -> {
      List<ErrorAttributes> errors = new ArrayList<>();
      errors.add(commonAttribute);
      errorAttributes.put(ERRORS_PROPERTY_NAME, errors);
    });

    log.error(errorAttributes.toString());
    return errorAttributes;
  }

}
