package ru.zubkoff.sber.cardcrud.api.exceptions;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ru.zubkoff.sber.cardcrud.core.exceptions.EntityNotFoundException;
import ru.zubkoff.sber.cardcrud.core.exceptions.NonUniqueValueException;

@ControllerAdvice
public class ControllerExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

  private static final Map<String, Object> buildResponse(String info, Object details) {
    return Map.of(
        "info", info,
        "details", details);
  }

  @ResponseBody
  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, Object> handleNotFoundException(EntityNotFoundException e) {
    LOGGER.error("EntityNotFoundException: " + e.getMessage());
    return buildResponse("entity not found", Objects.requireNonNullElse(e.getMessage(), ""));
  }

  @ResponseBody
  @ExceptionHandler(NonUniqueValueException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, Object> handleNonUniqueValue(NonUniqueValueException e) {
    LOGGER.error("NonUniqueValue: " + e.getMessage());
    return buildResponse("non unique value", Objects.requireNonNullElse(e.getMessage(), ""));
  }

  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException e) {
    var validationErrors = e.getBindingResult().getAllErrors().stream()
        .map(error -> error.getDefaultMessage())
        .toList();
    LOGGER.error("Validation error: " + validationErrors);
    return buildResponse("validation errors", validationErrors);
  }

}
