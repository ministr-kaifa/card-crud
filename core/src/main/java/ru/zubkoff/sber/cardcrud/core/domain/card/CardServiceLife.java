package ru.zubkoff.sber.cardcrud.core.domain.card;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import ru.zubkoff.sber.cardcrud.core.exceptions.InvariantConstraintViolationException;

@Embeddable
public record CardServiceLife(
    @Column(nullable = false, name = "valid_from") LocalDate validFrom,
    @Column(nullable = false, name = "valid_to") LocalDate validTo) {
  public CardServiceLife {
    if (validFrom.isAfter(validTo)) {
      throw new InvariantConstraintViolationException(
          "validTo(%s) is before validFrom(%s)".formatted(validFrom, validTo));
    }
  }
}

// @Embeddable
// public class CardServiceLife implements Serializable {

// @Column(nullable = false)
// private LocalDate validFrom;

// @Column(nullable = false)
// private LocalDate validTo;

// public CardServiceLife(LocalDate validFrom, LocalDate validTo) {
// Objects.requireNonNull(validTo);
// Objects.requireNonNull(validFrom);
// if (validFrom.isAfter(validTo)) {
// throw new InvariantConstraintViolationException("validTo is before
// validFrom");
// }
// this.validFrom = validFrom;
// this.validTo = validTo;
// }

// public LocalDate getValidFrom() {
// return validFrom;
// }

// public LocalDate getValidTo() {
// return validTo;
// }

// @Override
// public int hashCode() {
// return Objects.hash(validFrom, validTo);
// }

// @Override
// public boolean equals(Object obj) {
// if (obj == null) {
// return false;
// }
// if (this == obj) {
// return true;
// }
// if (obj instanceof CardServiceLife other) {
// return validFrom.equals(other.validFrom) && validTo.equals(other.validTo);
// }
// return false;
// }
// }
