package com.abnamro.care.event.in.error;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.abnamro.care.event.in.error.dto.ErrorAttributes;

import lombok.experimental.UtilityClass;



@UtilityClass
public class ErrorAttributesMapper {

  private static final String FIELD_VALIDATION_ERROR_MESSAGE = "%s %s in %s";
  private static final String OBJECT_VALIDATION_ERROR_MESSAGE = "%s for %s";

  public static List<ErrorAttributes> mapErrorAttributesFieldError(final List<ObjectError> objectErrors,
      final ErrorAttributes commonAttribute, final MessageSource messageSource) {
    List<ErrorAttributes> errors = null;

    if (objectErrors != null) {
      errors = objectErrors.stream().map(objectError -> {
        ErrorAttributes currentAttribute = new ErrorAttributes();
        BeanUtils.copyProperties(commonAttribute, currentAttribute);

        String defaultMessage = messageSource.getMessage(objectError, Locale.getDefault());
        String fieldName = null;
        if (objectError instanceof FieldError) {
          fieldName = ((FieldError) objectError).getField();
        }

        Optional.ofNullable(fieldName).ifPresentOrElse(fl -> currentAttribute.setMessage(
            String.format(FIELD_VALIDATION_ERROR_MESSAGE, fl, defaultMessage, objectError.getObjectName())),
            () -> currentAttribute.setMessage(
                String.format(OBJECT_VALIDATION_ERROR_MESSAGE, defaultMessage, objectError.getObjectName())));

        return currentAttribute;
      }).collect(Collectors.toList());
    }

    return errors;
  }
}
