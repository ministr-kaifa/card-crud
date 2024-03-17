package ru.zubkoff.sber.cardcrud.core.persistence;

import org.hibernate.exception.ConstraintViolationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ru.zubkoff.sber.cardcrud.core.domain.Client;
import ru.zubkoff.sber.cardcrud.core.exceptions.NonUniqueValue;

public class CustomizedClientRepositoryImpl implements CustomizedClientRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public void createClient(Client client) {
    try {
      entityManager.persist(client);
    } catch (ConstraintViolationException e) {
      if(e.getConstraintName().equals("unique_passport_number")) {
        throw new NonUniqueValue("Already have client with such passport number", e);
      } else if (e.getConstraintName().equals("unique_email")) {
        throw new NonUniqueValue("Already have client with such email", e);
      }
    }
  }
  
}
