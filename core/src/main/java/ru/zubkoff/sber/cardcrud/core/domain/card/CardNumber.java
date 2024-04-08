package ru.zubkoff.sber.cardcrud.core.domain.card;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ru.zubkoff.sber.cardcrud.core.exceptions.InvariantConstraintViolationException;

@Embeddable
public record CardNumber(
    @Column(nullable = false, name = "card_number") String value) {

  public static final String VALIDATION_REGEX = "\\d{16}";

  public CardNumber {
    if (!value.matches(VALIDATION_REGEX)) {
      throw new InvariantConstraintViolationException("invalid card number: " + value);
    }
  }

  @Override
  public final String toString() {
    return value;
  }
}
