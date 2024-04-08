package ru.zubkoff.sber.cardcrud.core.domain.card;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ru.zubkoff.sber.cardcrud.core.exceptions.InvariantConstraintViolationException;

@Embeddable
public record CardNumber(@Column(nullable = false, name = "card_number") String value) {
  public CardNumber {
    if (!value.matches("\\d{16}")) {
      throw new InvariantConstraintViolationException("invalid card number: " + value);
    }
  }
}

// @Embeddable
// public class CardNumber implements Serializable {

// @Column(nullable = false, name = "card_number")
// private final String value;

// public String getValue() {
// return value;
// }

// public CardNumber(String value) {
// Objects.requireNonNull(value);
// if (!value.matches("\\d{16}")) {
// throw new InvariantConstraintViolationException("invalid card value");
// }
// this.value = value;
// }

// @Override
// public int hashCode() {
// return Objects.hash(value);
// }

// @Override
// public boolean equals(Object obj) {
// if (obj == null) {
// return false;
// }
// if (this == obj) {
// return true;
// }
// if (obj instanceof CardNumber other) {
// return other.value.equals(this.value);
// }
// return false;
// }

// }
