package com.abnamro.care.event.in.error;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import com.abnamro.care.event.in.error.dto.Error;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.abnamro.care.event.in.error.dto.ErrorAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String NOT_READABLE_ERROR_MESSAGE = "User Input Not Readable";
  private static final String NOT_READABLE_ERROR_CODE = "ERR_NOT_READABLE";
  private static final String INTERNAL_SERVER_ERROR_CODE = "INTERNAL_SERVER_ERROR";
  private static final String METHOD_ARGUMENT_INVALID_ERROR_CODE = "ERR_METHOD_ARGUMENT_INVALID";
  private static final String METHOD_PARAMETER_MISSING_ERROR_CODE = "ERR_METHOD_PARAMETER_MISSING";
  private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error occurred";
  private static final String VALIDATION_ERROR_MESSAGE = "Input Validation Failed";
  private static final String VALIDATION_ERROR_CODE = "ERR_INPUT_VALIDATION";
  private static final String CONSTRAINT_INVALID_ERROR_CODE = "ERR_METHOD_ARGUMENT_INVALID";
  private static final String METHOD_INVALID_ERROR_CODE = "ERR_METHOD_NOT_SUPPORTED";
  private static final String MEDIA_TYPE_INVALID_ERROR_CODE = "ERR_MEDIA_TYPE_NOT_SUPPORTED";

  private static final String TRACE_ID_PROPERTY_NAME = "X-B3-TraceId";
  private final MessageSource messageSource;

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
      final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
    log.error(NOT_READABLE_ERROR_MESSAGE, ex);
    return new ResponseEntity<>(
        getErrorResponseEntity(NOT_READABLE_ERROR_CODE, NOT_READABLE_ERROR_MESSAGE, HttpStatus.BAD_REQUEST),
        HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
      final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

    log.error(VALIDATION_ERROR_MESSAGE, ex);
    BindingResult result = ex.getBindingResult();
    List<ObjectError> objectErrors = result.getAllErrors();

    ErrorAttributes commonAttribute = ErrorAttributes.builder().code(VALIDATION_ERROR_CODE)
        .status(HttpStatus.BAD_REQUEST.value()).traceId(MDC.get(TRACE_ID_PROPERTY_NAME)).build();

    List<ErrorAttributes> errorAttributesList =
        ErrorAttributesMapper.mapErrorAttributesFieldError(objectErrors, commonAttribute, messageSource);

    Error error = new Error();
    error.setErrors(errorAttributesList);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Error> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
      WebRequest request) {
    String errorMessage = String.format("For %s Expected Type is  %s ", ex.getName(), ex.getRequiredType().getName());

    return new ResponseEntity<>(
        getErrorResponseEntity(METHOD_ARGUMENT_INVALID_ERROR_CODE, errorMessage, HttpStatus.BAD_REQUEST),
        HttpStatus.BAD_REQUEST);

  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {

    List<ErrorAttributes> errors = ex.getConstraintViolations().parallelStream().map(violation -> {
      String errorMessage = violation.getPropertyPath() + ": " + violation.getMessage();

      return ErrorAttributes.builder().code(CONSTRAINT_INVALID_ERROR_CODE).traceId(MDC.get(TRACE_ID_PROPERTY_NAME))
          .message(errorMessage).status(HttpStatus.BAD_REQUEST.value()).build();

    }).collect(Collectors.toList());

    Error response = new Error();
    response.setErrors(errors);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    StringBuilder errorMessageBuilder = new StringBuilder();
    errorMessageBuilder.append(ex.getMethod());
    errorMessageBuilder.append(
        " method is not supported for this request. Supported methods are ");
    ex.getSupportedHttpMethods().forEach(t -> errorMessageBuilder.append(t + " "));

    return new ResponseEntity<>(
        getErrorResponseEntity(METHOD_INVALID_ERROR_CODE, errorMessageBuilder.toString(),
            HttpStatus.METHOD_NOT_ALLOWED),
        HttpStatus.METHOD_NOT_ALLOWED);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    StringBuilder errorMessageBuilder = new StringBuilder();
    errorMessageBuilder.append(ex.getContentType());
    errorMessageBuilder.append(" media type is not supported. Supported media types are ");
    ex.getSupportedMediaTypes().forEach(t -> errorMessageBuilder.append(t + ", "));

    return new ResponseEntity<>(
        getErrorResponseEntity(MEDIA_TYPE_INVALID_ERROR_CODE, errorMessageBuilder.toString(),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE),
        HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    String errorMessage = String.format("'%s' parameter is missing ", ex.getParameterName());

    return new ResponseEntity<>(
        getErrorResponseEntity(METHOD_PARAMETER_MISSING_ERROR_CODE, errorMessage, HttpStatus.BAD_REQUEST),
        HttpStatus.BAD_REQUEST);

  }

  @ExceptionHandler({NullPointerException.class, Exception.class})
  public ResponseEntity<Error> handleInternal(final RuntimeException ex, final WebRequest request) {
    log.error(INTERNAL_SERVER_ERROR_MESSAGE, ex);
    return new ResponseEntity<>(
        getErrorResponseEntity(INTERNAL_SERVER_ERROR_CODE, INTERNAL_SERVER_ERROR_MESSAGE,
            HttpStatus.INTERNAL_SERVER_ERROR),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private Error getErrorResponseEntity(String code, String message, HttpStatus status) {

    ErrorAttributes property = ErrorAttributes.builder().code(code).traceId(MDC.get(TRACE_ID_PROPERTY_NAME))
        .message(message).status(status.value()).build();

    com.abnamro.care.event.in.error.dto.Error response = new Error();
    List<ErrorAttributes> propertyList = new ArrayList<>();
    propertyList.add(property);
    response.setErrors(propertyList);

    return response;

  }

}