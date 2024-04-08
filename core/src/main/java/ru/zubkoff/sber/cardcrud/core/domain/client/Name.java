package ru.zubkoff.sber.cardcrud.core.domain.client;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ru.zubkoff.sber.cardcrud.core.exceptions.InvariantConstraintViolationException;

@Embeddable
public record Name(@Column(nullable = false, name = "name") String value) {

  public static final String VALIDATION_REGEX = "^(?=.{1,100}$)[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$";

  public Name {
    if (!value.matches(VALIDATION_REGEX)) {
      throw new InvariantConstraintViolationException("invalid name: " + value);
    }
  }

  @Override
  public final String toString() {
    return value;
  }
}
