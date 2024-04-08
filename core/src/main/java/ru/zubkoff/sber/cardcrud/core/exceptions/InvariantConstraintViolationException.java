package ru.zubkoff.sber.cardcrud.core.exceptions;

public class InvariantConstraintViolationException extends RuntimeException {

  public InvariantConstraintViolationException() {
    super();
  }

  public InvariantConstraintViolationException(String message) {
    super(message);
  }

  public InvariantConstraintViolationException(String message, Throwable cause) {
    super(message, cause);
  }

}
