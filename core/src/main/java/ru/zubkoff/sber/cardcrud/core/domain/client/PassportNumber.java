package ru.zubkoff.sber.cardcrud.core.domain.client;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ru.zubkoff.sber.cardcrud.core.exceptions.InvariantConstraintViolationException;

@Embeddable
public record PassportNumber(@Column(nullable = false, name = "passport_number") String value) {
  public PassportNumber {
    if (!value.matches("\\d{10}")) {
      throw new InvariantConstraintViolationException("invalid passport number: " + value);
    }
  }
}

// @Embeddable
// public class PassportNumber implements Serializable {
// @Column(nullable = false, name = "passport_number")
// private final String value;

// public String getValue() {
// return value;
// }

// public PassportNumber(String value) {
// Objects.requireNonNull(value);
// if (!value.matches("\\d{10}")) {
// throw new InvariantConstraintViolationException("invalid passport number");
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
// if (obj instanceof PassportNumber other) {
// return other.value.equals(this.value);
// }
// return false;
// }
// }
