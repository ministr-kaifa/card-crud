package ru.zubkoff.sber.cardcrud.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityNotFoundException;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardCreation;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardNumber;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardServiceLife;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardUpdate;
import ru.zubkoff.sber.cardcrud.core.domain.client.ClientCreation;
import ru.zubkoff.sber.cardcrud.core.domain.client.EmailAddress;
import ru.zubkoff.sber.cardcrud.core.domain.client.Name;
import ru.zubkoff.sber.cardcrud.core.domain.client.PassportNumber;
import ru.zubkoff.sber.cardcrud.core.persistence.ClientRepository;

@Testcontainers
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CardServiceTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

  private final CardServiceLife validServiceLife = new CardServiceLife(LocalDate.of(2000, 1, 1),
      LocalDate.of(3000, 1, 1));

  @Autowired
  CardService cardService;

  @Autowired
  ClientService clientService;

  @Autowired
  ClientRepository clientRepository;

  @BeforeEach
  void init() {
    clientRepository.deleteAll();
  }

  List<CardNumber> cardNumberRange(long length) {
    return LongStream.range(0, length)
        .mapToObj(number -> new CardNumber(String.format("%016d", number)))
        .toList();
  }

  @Test
  void givenCardsToReissue_whenReissue_thenUnableToFindOldCards() {
    // given
    var clientCreation = new ClientCreation(
        new PassportNumber("4444666666"),
        new Name("Иванов Иван Иванович"),
        new EmailAddress("email@email.com"),
        cardNumberRange(100).stream()
            .map(cardNumber -> new CardCreation(cardNumber, validServiceLife))
            .toList());
    var client = clientService.createClient(clientCreation);// now client object is detached

    // when
    client.getCards().forEach(card -> cardService.reissueCard(card));

    // then
    client.getCards()
        .forEach(card -> assertThrows(EntityNotFoundException.class, () -> cardService.findCardById(card.getId())));
  }

  @Test
  void givenCardsToReissue_whenReissue_thenOwnerCardAmountSame() {
    // given
    var clientCreation = new ClientCreation(
        new PassportNumber("4444666666"),
        new Name("Иванов Иван Иванович"),
        new EmailAddress("email@email.com"),
        cardNumberRange(100).stream()
            .map(cardNumber -> new CardCreation(cardNumber, validServiceLife))
            .toList());

    var client = clientService.createClient(clientCreation);
    var cardAmountBeforeReissue = client.getCards().size();

    // when
    client.getCards().forEach(card -> cardService.reissueCard(card));
    var cardAmountAfterReissue = clientService.findClientWithCardsById(client.getId()).getCards().size();

    // then
    assertEquals(cardAmountBeforeReissue, cardAmountAfterReissue);
  }

  @Test
  void givenCardToUpdate_whenUpdatingCard_thenOnlyUpdatingFieldsUpdated() {
    // given
    var clientCreation = new ClientCreation(
        new PassportNumber("6666444444"),
        new Name("Иванов Иван Иванович"),
        new EmailAddress("email@email.com"));
    var oldCardNumber = new CardNumber("1111222233334444");
    var cardCreation = new CardCreation(oldCardNumber, validServiceLife);
    var owner = clientService.createClient(clientCreation);
    var cardBeforeUpdate = cardService.createCard(cardCreation, owner.getId());

    var newCardNumber = new CardNumber("1111111111111111");
    var cardUpdate = new CardUpdate(
        Optional.empty(),
        Optional.of(newCardNumber),
        Optional.empty(),
        Optional.empty());

    // when
    var cardAfterUpdate = cardService.updateById(cardBeforeUpdate.getId(), cardUpdate);

    // then
    assertEquals(oldCardNumber, cardBeforeUpdate.getCardNumber());
    assertEquals(newCardNumber, cardAfterUpdate.getCardNumber());
    assertEquals(cardBeforeUpdate.getId(), cardAfterUpdate.getId());
    assertEquals(cardBeforeUpdate.getServiceLife(), cardBeforeUpdate.getServiceLife());
    assertEquals(cardBeforeUpdate.getOwner(), cardAfterUpdate.getOwner());
  }

  @Test
  void giveCardToUpdate_whenEmptyUpdate_thenNoChanges() {
    // given
    var clientCreation = new ClientCreation(
        new PassportNumber("6666444444"),
        new Name("Иванов Иван Иванович"),
        new EmailAddress("email@email.com"));
    var cardCreation = new CardCreation(new CardNumber("1111222233334444"), validServiceLife);
    var owner = clientService.createClient(clientCreation);
    var cardBeforeUpdate = cardService.createCard(cardCreation, owner.getId());

    var cardUpdate = new CardUpdate(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());

    // when
    var cardAfterUpdate = cardService.updateById(cardBeforeUpdate.getId(), cardUpdate);

    // then
    assertEquals(cardBeforeUpdate.getId(), cardAfterUpdate.getId());
    assertEquals(cardBeforeUpdate.getCardNumber(), cardAfterUpdate.getCardNumber());
    assertEquals(cardBeforeUpdate.getServiceLife(), cardBeforeUpdate.getServiceLife());
    assertEquals(cardBeforeUpdate.getOwner(), cardAfterUpdate.getOwner());
  }

  @Test
  void givenCard_whenUpdateOwner_thenNewOwnerOfCard() {
    // given
    var oldOwnerCreation = new ClientCreation(
        new PassportNumber("6666444444"),
        new Name("Иванов Иван Иванович"),
        new EmailAddress("email@email.com"),
        List.of(new CardCreation(new CardNumber("1111222233334444"), validServiceLife)));
    var oldOwner = clientService.createClient(oldOwnerCreation);
    var cardBeforeUpdate = oldOwner.getCards().get(0);

    var newOwnerCreation = new ClientCreation(
        new PassportNumber("4444666666"),
        new Name("Иванов Иван Иванович"),
        new EmailAddress("email@mail.com"));
    var newOwner = clientService.createClient(newOwnerCreation);

    var cardUpdate = new CardUpdate(
        Optional.of(newOwner.getId()),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());

    // when
    var cardAfterUpdate = cardService.updateById(cardBeforeUpdate.getId(), cardUpdate);

    // then
    assertEquals(oldOwner, cardBeforeUpdate.getOwner());
    assertEquals(newOwner, cardAfterUpdate.getOwner());
    assertEquals(cardBeforeUpdate.getId(), cardAfterUpdate.getId());
    assertEquals(cardBeforeUpdate.getCardNumber(), cardAfterUpdate.getCardNumber());
    assertEquals(cardBeforeUpdate.getServiceLife(), cardBeforeUpdate.getServiceLife());
  }

}
