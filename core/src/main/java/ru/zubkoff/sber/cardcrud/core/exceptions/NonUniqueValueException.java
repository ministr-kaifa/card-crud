package ru.zubkoff.sber.cardcrud.core.exceptions;

public class NonUniqueValueException extends InvariantConstraintViolationException {

  public NonUniqueValueException(String message) {
    super(message);
  }

  public NonUniqueValueException(String message, Throwable cause) {
    super(message, cause);
  }
}
