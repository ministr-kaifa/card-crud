package ru.zubkoff.sber.cardcrud.core.persistence;

import ru.zubkoff.sber.cardcrud.core.domain.Card;

public interface CustomizedCardRepository {
  void createCard(Card card, Long ownerClientId); 
}