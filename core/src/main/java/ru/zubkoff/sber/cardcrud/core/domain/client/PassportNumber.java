package ru.zubkoff.sber.cardcrud.core.domain.client;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ru.zubkoff.sber.cardcrud.core.exceptions.InvariantConstraintViolationException;

@Embeddable
public record PassportNumber(@Column(nullable = false, name = "passport_number") String value) {

  public static final String VALIDATION_REGEX = "\\d{10}";

  public PassportNumber {
    if (!value.matches(VALIDATION_REGEX)) {
      throw new InvariantConstraintViolationException("invalid passport number: " + value);
    }
  }

  @Override
  public final String toString() {
    return value;
  }
}
