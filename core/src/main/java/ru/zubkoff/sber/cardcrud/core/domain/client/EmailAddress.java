package ru.zubkoff.sber.cardcrud.core.domain.client;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ru.zubkoff.sber.cardcrud.core.exceptions.InvariantConstraintViolationException;

@Embeddable
public record EmailAddress(@Column(nullable = false, name = "email") String value) {
  public EmailAddress {
    if (!value.matches(".+@.+")) {
      throw new InvariantConstraintViolationException("invalid email: " + value);
    }
  }
}

// @Embeddable
// public class EmailAddress implements Serializable {
// @Column(nullable = false, name = "email")
// private final String value;

// public String getValue() {
// return value;
// }

// public EmailAddress(String value) {
// Objects.requireNonNull(value);
// if (!value.matches(".+@.+")) {
// throw new InvariantConstraintViolationException("invalid email");
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
// if (obj instanceof EmailAddress other) {
// return other.value.equals(this.value);
// }
// return false;
// }
// }
