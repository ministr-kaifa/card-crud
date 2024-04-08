package ru.zubkoff.sber.cardcrud.eventmanager.events.card.expired;

import org.springframework.context.ApplicationEvent;

import ru.zubkoff.sber.cardcrud.core.domain.card.Card;
import ru.zubkoff.sber.cardcrud.core.domain.client.Client;

public class CardExpiredEvent extends ApplicationEvent {
  private final Client client;
  private final Card card;

  public CardExpiredEvent(Object source, Client client, Card card) {
    super(source);
    this.client = client;
    this.card = card;
  }

  public Card getCard() {
    return card;
  }

  public Client getClient() {
    return client;
  }

}
