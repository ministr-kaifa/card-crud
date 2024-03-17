package ru.zubkoff.sber.cardcrud.core.exceptions;

public class NonUniqueValue extends RuntimeException {

  public NonUniqueValue(String message) {
    super(message);
  }

  public NonUniqueValue(String message, Throwable cause) {
    super(message, cause);
  }
}
