package ru.zubkoff.sber.cardcrud.core.domain.card;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import ru.zubkoff.sber.cardcrud.core.domain.client.Client;
import ru.zubkoff.sber.cardcrud.core.exceptions.InvariantConstraintViolationException;

public class CardUpdate {

  private Client newOwner;
  private final Optional<Long> newOwnerId;
  private final Optional<CardNumber> newCardNumber;
  private final Optional<LocalDate> newValidFrom;
  private final Optional<LocalDate> newValidTo;

  public CardUpdate(Optional<Long> newOwnerId, Optional<CardNumber> cardNumber, Optional<LocalDate> validFrom,
      Optional<LocalDate> validTo) {
    if (!((validFrom.isEmpty() || validTo.isEmpty()) || validFrom.get().isBefore(validTo.get()))) {
      throw new InvariantConstraintViolationException("validFrom is after validTo");
    }
    this.newCardNumber = cardNumber;
    this.newValidFrom = validFrom;
    this.newValidTo = validTo;
    this.newOwnerId = newOwnerId;
  }

  public void fetchOwner(Client owner) {
    Objects.requireNonNull(owner);
    if (newOwnerId.isEmpty()) {
      throw new RuntimeException("fetching unknown owner");
    }

    if (owner.getId() == null || owner.getId().equals(newOwnerId.get())) {
      newOwner = owner;
    } else {
      throw new RuntimeException("fetching owner with different id");
    }
  }

  public void apply(Card card) {
    if (newOwnerId.isPresent()) {
      if (newOwner == null) {
        throw new RuntimeException("owner is not fetched");
      }
      card.setOwner(newOwner);
    }
    newCardNumber.ifPresent(card::setCardNumber);
    var newServiceLife = new CardServiceLife(
        newValidFrom.orElse(card.getServiceLife().validFrom()),
        newValidTo.orElse(card.getServiceLife().validTo()));
    if (!newServiceLife.equals(card.getServiceLife())) {
      card.setServiceLife(newServiceLife);
    }
  }

  public Optional<CardNumber> getNewCardNumber() {
    return newCardNumber;
  }

  public Optional<LocalDate> getNewValidFrom() {
    return newValidFrom;
  }

  public Optional<LocalDate> getNewValidTo() {
    return newValidTo;
  }

  public Optional<Long> getNewOwnerId() {
    return newOwnerId;
  }
}
