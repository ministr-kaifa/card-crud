package ru.zubkoff.sber.cardcrud.core.domain.client;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ru.zubkoff.sber.cardcrud.core.exceptions.InvariantConstraintViolationException;

@Embeddable
public record Name(@Column(nullable = false, name = "name") String value) {
  public Name {
    if (!value.matches("^(?=.{1,100}$)[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$")) {
      throw new InvariantConstraintViolationException("invalid name: " + value);
    }
  }
}

// @Embeddable
// public class Name implements Serializable {
// @Column(nullable = false, name = "name")
// private String value;

// public Name() {
// }

// public String value() {
// return value;
// }

// public Name(String value) {
// Objects.requireNonNull(value);
// if (!value.matches("^(?=.{1,100}$)[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$")) {
// throw new InvariantConstraintViolationException("invalid name");
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
// if (obj instanceof Name other) {
// return other.value.equals(this.value);
// }
// return false;
// }
// }
