package ru.zubkoff.sber.cardcrud.core.domain.card;

import java.util.Objects;

import ru.zubkoff.sber.cardcrud.core.domain.client.Client;

public class CardCreation {

  private final CardNumber cardNumber;
  private final CardServiceLife serviceLife;

  private Client owner;
  private boolean isOwnerFetched;

  public CardCreation(Client owner, CardNumber cardNumber, CardServiceLife serviceLife) {
    this(cardNumber, serviceLife);
    Objects.requireNonNull(owner);

    this.owner = owner;
    this.isOwnerFetched = true;
  }

  public CardCreation(CardNumber cardNumber, CardServiceLife serviceLife) {
    Objects.requireNonNull(cardNumber);
    Objects.requireNonNull(serviceLife);

    this.cardNumber = cardNumber;
    this.serviceLife = serviceLife;
    this.isOwnerFetched = false;
  }

  public Card toEntity() {
    if (!isOwnerFetched) {
      throw new RuntimeException("owner is not fetched");
    }
    return new Card(null, owner, cardNumber, serviceLife);
  }

  public void fetchOwner(Client owner) {
    Objects.requireNonNull(owner);
    if (isOwnerFetched) {
      throw new RuntimeException("owner already fetched");
    }

    this.owner = owner;
    this.isOwnerFetched = true;
  }

  public CardNumber getCardNumber() {
    return cardNumber;
  }

  public CardServiceLife getServiceLife() {
    return serviceLife;
  }
}
