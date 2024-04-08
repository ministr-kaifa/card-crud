package ru.zubkoff.sber.cardcrud.core.persistence;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ru.zubkoff.sber.cardcrud.core.domain.client.Client;
import ru.zubkoff.sber.cardcrud.core.domain.client.EmailAddress;
import ru.zubkoff.sber.cardcrud.core.domain.client.Name;
import ru.zubkoff.sber.cardcrud.core.domain.client.PassportNumber;
import ru.zubkoff.sber.cardcrud.core.exceptions.NonUniqueValueException;

@DataJpaTest(showSql = true)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

  @Autowired
  ClientRepository clientRepository;

  @Test
  void given2ClientsWithSamePassportNumber_whenCreateClients_thenThrowNonUniqueValues() {
    // given
    var samePassportNumber = new PassportNumber("4444666666");

    var client1 = new Client(
        null,
        samePassportNumber,
        new Name("Иванов Иван Иванович"),
        new EmailAddress("email@email.com"),
        List.of());

    var client2 = new Client(
        null,
        samePassportNumber,
        new Name("Петров Петр Петрович"),
        new EmailAddress("email@email.ru"),
        List.of());

    // when
    clientRepository.createClient(client1);

    // then
    assertThrows(NonUniqueValueException.class, () -> clientRepository.createClient(client2));
  }

  @Test
  void given2ClientsWithSameEmail_whenCreateClients_thenThrowNonUniqueValues() {
    // given
    var sameEmail = new EmailAddress("email@email.com");
    var client1 = new Client(
        null,
        new PassportNumber("1111111111"),
        new Name("Иванов Иван Иванович"),
        sameEmail,
        List.of());

    var client2 = new Client(
        null,
        new PassportNumber("2222222222"),
        new Name("Петров Петр Петрович"),
        sameEmail,
        List.of());

    // when
    clientRepository.createClient(client1);

    // then
    assertThrows(NonUniqueValueException.class, () -> clientRepository.createClient(client2));
  }

  @Test
  void givenClient_whenCreateClient_thenAbleToFindClient() {
    // given
    var client = new Client(
        null,
        new PassportNumber("1111111111"),
        new Name("Иванов Иван Иванович"),
        new EmailAddress("email@email.com"),
        List.of());

    // when
    clientRepository.createClient(client);
    var clientFromRepository = clientRepository.findById(client.getId());

    // then
    assertTrue(clientFromRepository.isPresent());
    assertEquals(client, clientFromRepository.get());
  }

}
