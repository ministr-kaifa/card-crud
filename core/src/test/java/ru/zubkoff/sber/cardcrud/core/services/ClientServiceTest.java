package ru.zubkoff.sber.cardcrud.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ru.zubkoff.sber.cardcrud.core.domain.Client;
import ru.zubkoff.sber.cardcrud.core.persistence.ClientRepository;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientServiceTest {
  
  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

  @Autowired
  ClientRepository clientRepository;

  @Autowired
  ClientService clientService;

  @BeforeEach
  void init() {
    clientRepository.deleteAll();
  }

  @Test
  void givenClientToMerge_whenMerge_thenAllNonNullMergeClientFieldPersisted() {
    //given
    var mergeFrom = new Client(null, "1111111111", "Иванов Иван Иванович", "email@email.com", List.of());
    var mergeTo = new Client(null, "2222222222", "Петров Петр Петрович", "abcd@abcd.abcd", List.of());

    //when
    clientService.createClient(mergeTo);
    clientService.mergeById(mergeTo.getId(), mergeFrom);
    var mergedCard = clientService.findClientWithCardsById(mergeTo.getId());

    //then
    assertEquals(mergeFrom.getEmail(), mergedCard.getEmail());
    assertEquals(mergeFrom.getName(), mergedCard.getName());
    assertEquals(mergeFrom.getPassportNumber(), mergedCard.getPassportNumber());
  }

  @Test
  void givenEmptyClientToMerge_whenMerge_thenNoChanges() {
    //given
    var mergeFrom = new Client(null, null, null, null, List.of());
    var mergeTo = new Client(null, "2222222222", "Петров Петр Петрович", "abcd@abcd.abcd", List.of());

    //when
    clientService.createClient(mergeTo);
    clientService.mergeById(mergeTo.getId(), mergeFrom);
    var mergedCard = clientService.findClientWithCardsById(mergeTo.getId());

    //then
    assertEquals(mergeTo.getEmail(), mergedCard.getEmail());
    assertEquals(mergeTo.getName(), mergedCard.getName());
    assertEquals(mergeTo.getPassportNumber(), mergedCard.getPassportNumber());
  }
}
