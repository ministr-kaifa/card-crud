package ru.zubkoff.sber.cardcrud.core.domain.client;

import java.util.List;
import java.util.Objects;

import ru.zubkoff.sber.cardcrud.core.domain.card.CardCreation;

public class ClientCreation {
  private final PassportNumber passportNumber;
  private final Name name;
  private final EmailAddress email;
  private final List<CardCreation> cards;

  public ClientCreation(PassportNumber passportNumber, Name name, EmailAddress email, List<CardCreation> cards) {
    Objects.requireNonNull(passportNumber);
    Objects.requireNonNull(name);
    Objects.requireNonNull(email);
    Objects.requireNonNull(cards);

    this.passportNumber = passportNumber;
    this.name = name;
    this.email = email;
    this.cards = cards;
  }

  public ClientCreation(PassportNumber passportNumber, Name name, EmailAddress email) {
    Objects.requireNonNull(passportNumber);
    Objects.requireNonNull(name);
    Objects.requireNonNull(email);

    this.passportNumber = passportNumber;
    this.name = name;
    this.email = email;
    this.cards = List.of();
  }

  public Client toEntity() {
    Objects.requireNonNull(passportNumber);
    Objects.requireNonNull(name);
    Objects.requireNonNull(email);

    var result = new Client(null, passportNumber, name, email, List.of());
    cards.forEach(card -> card.fetchOwner(result));
    result.setCards(
        cards.stream()
            .map(CardCreation::toEntity)
            .toList());
    return result;
  }

}
