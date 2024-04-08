package ru.zubkoff.sber.cardcrud.core.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ru.zubkoff.sber.cardcrud.core.domain.card.Card;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardNumber;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardServiceLife;
import ru.zubkoff.sber.cardcrud.core.domain.client.Client;
import ru.zubkoff.sber.cardcrud.core.domain.client.EmailAddress;
import ru.zubkoff.sber.cardcrud.core.domain.client.Name;
import ru.zubkoff.sber.cardcrud.core.domain.client.PassportNumber;
import ru.zubkoff.sber.cardcrud.core.exceptions.EntityNotFoundException;
import ru.zubkoff.sber.cardcrud.core.exceptions.NonUniqueValueException;

@Testcontainers
@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CardRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

  @Autowired
  ClientRepository clientRepository;

  @Autowired
  CardRepository cardRepository;

  Client client() {
    return new Client(
        null,
        new PassportNumber("1234567890"),
        new Name("Иванов Иван Иванович"),
        new EmailAddress("ivanov@example.com"),
        List.of());
  }

  Card card() {
    return new Card(
        null,
        null,
        new CardNumber("1111222233334444"),
        new CardServiceLife(LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1)));
  }

  @Test
  void givenCardWithExistingOwner_whenCreateCard_thenAbleToFindCard() {
    // given
    var client = client();
    var card = card();

    // when
    clientRepository.createClient(client);
    cardRepository.createCard(card, client.getId());
    var cardFromRepository = cardRepository.findById(card.getId());

    // then
    assertTrue(cardFromRepository.isPresent());
    assertEquals(card, cardFromRepository.get());
  }

  @Test
  void givenCardWithNonExistingOwner_whenCreateCard_thenThrowsEntityNotFound() {
    // given
    var nonExistingOwnerId = 100L;
    var card = card();
    clientRepository.deleteById(nonExistingOwnerId);

    // when then
    assertThrows(EntityNotFoundException.class, () -> cardRepository.createCard(card, nonExistingOwnerId));
  }

  @Test
  void given2CardsWithSameCardNumber_whenCreateCards_thenThrowsNonUniqueValue() {
    // given
    var client = client();
    var card1 = card();
    var card2 = card();
    clientRepository.createClient(client);
    cardRepository.createCard(card1, client.getId());

    // when then
    assertThrows(NonUniqueValueException.class, () -> cardRepository.createCard(card2, client.getId()));
  }
}
