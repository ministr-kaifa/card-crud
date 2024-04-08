package ru.zubkoff.sber.cardcrud.core.persistence;

import org.hibernate.exception.ConstraintViolationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.zubkoff.sber.cardcrud.core.domain.card.Card;
import ru.zubkoff.sber.cardcrud.core.domain.client.Client;
import ru.zubkoff.sber.cardcrud.core.exceptions.EntityNotFoundException;
import ru.zubkoff.sber.cardcrud.core.exceptions.NonUniqueValueException;

public class CustomizedCardRepositoryImpl implements CustomizedCardRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public void createCard(Card card, Long ownerClientId) {
    Client owner = entityManager.find(Client.class, ownerClientId);
    if (owner == null) {
      throw new EntityNotFoundException("Cant crate card: no client with such id");
    }
    card.setOwner(owner);
    try {
      entityManager.persist(card);
    } catch (ConstraintViolationException e) {
      if (e.getConstraintName().equals("unique_card_number")) {
        throw new NonUniqueValueException("Already have card with such card number", e);
      }
    }
  }

}
