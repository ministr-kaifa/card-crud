package ru.zubkoff.sber.cardcrud.core.domain.client;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ru.zubkoff.sber.cardcrud.core.exceptions.InvariantConstraintViolationException;

@Embeddable
public record EmailAddress(@Column(nullable = false, name = "email") String value) {

  public static final String VALIDATION_REGEX = ".+@.+";

  public EmailAddress {
    if (!value.matches(VALIDATION_REGEX)) {
      throw new InvariantConstraintViolationException("invalid email: " + value);
    }
  }

  @Override
  public final String toString() {
    return value;
  }
}
