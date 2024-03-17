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

import ru.zubkoff.sber.cardcrud.core.domain.Client;
import ru.zubkoff.sber.cardcrud.core.exceptions.NonUniqueValue;


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
    //given
    var samePassPortNumber = "4444666666";
    var client1 = new Client(null, samePassPortNumber, "Иванов Иван Иванович", "email@email.com", List.of());
    var client2 = new Client(null, samePassPortNumber, "Петров Петр Петрович", "email@email.ru", List.of());

    //when
    clientRepository.createClient(client1);

    //then
    assertThrows(NonUniqueValue.class, () -> clientRepository.createClient(client2));
  }

  @Test
  void given2ClientsWithSameEmail_whenCreateClients_thenThrowNonUniqueValues() {
    //given
    var sameEmail = "email@email.com";
    var client1 = new Client(null, "1234123456", "Иванов Иван Иванович", sameEmail, List.of());
    var client2 = new Client(null, "0000000000", "Петров Петр Петрович", sameEmail, List.of());

    //when
    clientRepository.createClient(client1);

    //then
    assertThrows(NonUniqueValue.class, () -> clientRepository.createClient(client2));
  }

  @Test
  void givenClient_whenCreateClient_thenAbleToFindClient() {
    //given
    var client = new Client(null, "1234123456", "Иванов Иван Иванович", "email@email.com", List.of());

    //when
    clientRepository.createClient(client);
    var clientFromRepository = clientRepository.findById(client.getId());

    //then
    assertTrue(clientFromRepository.isPresent());
    assertEquals(client, clientFromRepository.get());
  }

}
