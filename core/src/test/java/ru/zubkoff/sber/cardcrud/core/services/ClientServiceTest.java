package ru.zubkoff.sber.cardcrud.core.services;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ru.zubkoff.sber.cardcrud.core.domain.card.CardCreation;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardNumber;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardServiceLife;
import ru.zubkoff.sber.cardcrud.core.domain.client.ClientCreation;
import ru.zubkoff.sber.cardcrud.core.domain.client.ClientUpdate;
import ru.zubkoff.sber.cardcrud.core.domain.client.EmailAddress;
import ru.zubkoff.sber.cardcrud.core.domain.client.Name;
import ru.zubkoff.sber.cardcrud.core.domain.client.PassportNumber;
import ru.zubkoff.sber.cardcrud.core.persistence.ClientRepository;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientServiceTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

  private final CardServiceLife validServiceLife = new CardServiceLife(LocalDate.of(2000, 1, 1),
      LocalDate.of(3000, 1, 1));

  @Autowired
  ClientRepository clientRepository;

  @Autowired
  ClientService clientService;

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
  void givenClientToUpdate_whenUpdatingClient_thenOnlyUpdatingFieldsUpdated() {
    // given
    var oldPassportNumber = new PassportNumber("6666444444");
    var clientCreation = new ClientCreation(
        oldPassportNumber,
        new Name("Иванов Иван Иванович"),
        new EmailAddress("email@email.com"),
        cardNumberRange(10).stream()
            .map(cardNumber -> new CardCreation(cardNumber, validServiceLife))
            .toList());
    var clientBeforeUpdate = clientService.createClient(clientCreation);
    var newPassportNumber = new PassportNumber("4444666666");
    var clientUpdate = new ClientUpdate(
        Optional.of(newPassportNumber),
        Optional.empty(),
        Optional.empty());

    // when
    clientService.updateById(clientBeforeUpdate.getId(), clientUpdate);
    var clientAfterUpdate = clientService.findClientWithCardsById(clientBeforeUpdate.getId());

    // then
    assertEquals(oldPassportNumber, clientBeforeUpdate.getPassportNumber());
    assertEquals(newPassportNumber, clientAfterUpdate.getPassportNumber());
    assertEquals(clientBeforeUpdate.getId(), clientAfterUpdate.getId());
    assertEquals(clientBeforeUpdate.getName(), clientAfterUpdate.getName());
    assertEquals(clientBeforeUpdate.getEmail(), clientAfterUpdate.getEmail());
    assertArrayEquals(clientBeforeUpdate.getCards().toArray(), clientAfterUpdate.getCards().toArray());
  }

  @Test
  void giveClientToUpdate_whenEmptyUpdate_thenNoChanges() {
    // given
    var clientCreation = new ClientCreation(
        new PassportNumber("6666444444"),
        new Name("Иванов Иван Иванович"),
        new EmailAddress("email@email.com"),
        cardNumberRange(10).stream()
            .map(cardNumber -> new CardCreation(cardNumber, validServiceLife))
            .toList());
    var clientBeforeUpdate = clientService.createClient(clientCreation);
    var clientUpdate = new ClientUpdate(
        Optional.empty(),
        Optional.empty(),
        Optional.empty());

    // when
    clientService.updateById(clientBeforeUpdate.getId(), clientUpdate);
    var clientAfterUpdate = clientService.findClientWithCardsById(clientBeforeUpdate.getId());

    // then
    assertEquals(clientBeforeUpdate.getId(), clientAfterUpdate.getId());
    assertEquals(clientBeforeUpdate.getPassportNumber(), clientAfterUpdate.getPassportNumber());
    assertEquals(clientBeforeUpdate.getName(), clientAfterUpdate.getName());
    assertEquals(clientBeforeUpdate.getEmail(), clientAfterUpdate.getEmail());
    assertArrayEquals(clientBeforeUpdate.getCards().toArray(), clientAfterUpdate.getCards().toArray());
  }
}
