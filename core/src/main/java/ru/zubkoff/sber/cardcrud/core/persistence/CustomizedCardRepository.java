package ru.zubkoff.sber.cardcrud.core.persistence;

import ru.zubkoff.sber.cardcrud.core.domain.card.Card;

public interface CustomizedCardRepository {
  Card createCard(Card card, Long ownerClientId);

  Card createCard(Card card);
}
