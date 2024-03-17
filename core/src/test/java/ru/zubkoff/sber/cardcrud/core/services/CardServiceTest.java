package ru.zubkoff.sber.cardcrud.core.services;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
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

import ru.zubkoff.sber.cardcrud.core.domain.Card;
import ru.zubkoff.sber.cardcrud.core.domain.Client;
import ru.zubkoff.sber.cardcrud.core.exceptions.EntityNotFoundException;
import ru.zubkoff.sber.cardcrud.core.persistence.ClientRepository;

@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SpringBootTest
class CardServiceTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

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

  List<String> cardNumberRange(long length) {
    return LongStream.range(0, length)
      .mapToObj(number -> String.format("%016d", number))
      .toList();
  }

  @Test
  void givenCardsToReissue_whenReissue_thenUnableToFindOldCards() {
    //given
    var client = new Client(null, "4444666666", "Иванов Иван Иванович", "email@email.com", null);
    var reissuedCards = cardNumberRange(100).stream()
      .map(cardNumber -> new Card(null, client, cardNumber, LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1)))
      .toList();
    client.setCards(reissuedCards);
    clientService.createClient(client);

    //when
    reissuedCards.forEach(card -> cardService.reissueCard(card));

    //then
    reissuedCards.forEach(card -> assertThrows(EntityNotFoundException.class,() -> cardService.findCardById(card.getId())));
  }

  @Test
  void givenCardsToReissue_whenReissue_thenOwnerCardAmountSame() {
    //given
    var client = new Client(null, "4444666666", "Иванов Иван Иванович", "email@email.com", null);
    var reissuedCards = cardNumberRange(100).stream()
      .map(cardNumber -> new Card(null, client, cardNumber, LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1)))
      .toList();
    client.setCards(reissuedCards);
    clientService.createClient(client);

    //when
    reissuedCards.forEach(card -> cardService.reissueCard(card));

    //then
    assertEquals(reissuedCards.size(), clientService.findClientWithCardsById(client.getId()).getCards().size());
  }

  @Test
  void givenCardToMerge_whenMerge_thenAllNonNullMergeClientFieldPersisted() {
    //given
    var oldOwner = new Client(null, "1111111111", "Иванов Иван Иванович", "email@email.com", List.of());
    var newOwner = new Client(null, "2222222222", "Петров Петр Петрович", "abcd@abcd.abcd", List.of());
    var mergeFrom = new Card(null, newOwner, cardNumberRange(1).get(0), LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1));
    var mergeTo = new Card(null, oldOwner, cardNumberRange(2).get(1), LocalDate.of(5000, 1, 1), LocalDate.of(6000, 1, 1));
    clientService.createClient(oldOwner);
    clientService.createClient(newOwner);

    //when
    cardService.createCard(mergeTo, oldOwner.getId());
    cardService.mergeById(mergeTo.getId(), mergeFrom);
    var mergedCard = cardService.findCardById(mergeTo.getId());

    //then
    assertEquals(mergeFrom.getCardNumber(), mergedCard.getCardNumber());
    assertEquals(mergeFrom.getOwner().getId(), mergedCard.getOwner().getId());
    assertEquals(mergeFrom.getValidFrom(), mergedCard.getValidFrom());
    assertEquals(mergeFrom.getValidTo(), mergedCard.getValidTo());
  }

  @Test
  void givenEmptyCardToMerge_whenMerge_thenNoChanges() {
    //given
    var owner = new Client(null, "1111111111", "Иванов Иван Иванович", "email@email.com", List.of());
    var mergeFrom = new Card(null, null, null, null, null);
    var mergeTo = new Card(null, owner, cardNumberRange(1).get(0), LocalDate.of(5000, 1, 1), LocalDate.of(6000, 1, 1));
    clientService.createClient(owner);

    //when
    cardService.createCard(mergeTo, owner.getId());
    cardService.mergeById(mergeTo.getId(), mergeFrom);
    var mergedCard = cardService.findCardById(mergeTo.getId());

    //then
    assertEquals(mergeTo.getCardNumber(), mergedCard.getCardNumber());
    assertEquals(mergeTo.getOwner().getId(), mergedCard.getOwner().getId());
    assertEquals(mergeTo.getValidFrom(), mergedCard.getValidFrom());
    assertEquals(mergeTo.getValidTo(), mergedCard.getValidTo());

  }
  
}
