package ru.zubkoff.sber.cardcrud.core.services;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.zubkoff.sber.cardcrud.core.domain.client.Client;
import ru.zubkoff.sber.cardcrud.core.domain.client.ClientCreation;
import ru.zubkoff.sber.cardcrud.core.domain.client.ClientUpdate;
import ru.zubkoff.sber.cardcrud.core.exceptions.EntityNotFoundException;
import ru.zubkoff.sber.cardcrud.core.persistence.ClientRepository;

@Service
public class ClientService {

  private final ClientRepository clientRepository;

  public ClientService(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public Client findClientById(long clientId) {
    return clientRepository.findById(clientId).orElseThrow(
        () -> {
          throw new EntityNotFoundException("No client with id = " + clientId);
        });
  }

  public Client findClientWithCardsById(long clientId) {
    return clientRepository.findClientWithCardsById(clientId).orElseThrow(
        () -> {
          throw new EntityNotFoundException("No client with id = " + clientId);
        });
  }

  public void deleteClient(long clientId) {
    clientRepository.deleteById(clientId);
  }

  @Transactional
  public List<Client> findAllClients(int page, int pageSize) {
    return clientRepository.findAll(PageRequest.of(page, pageSize)).toList();
  }

  @Transactional
  public Client createClient(ClientCreation client) {
    var managedClient = client.toEntity();
    clientRepository.createClient(managedClient);
    return managedClient;
  }

  @Transactional
  public Client updateClient(long clientId, ClientUpdate update) {
    var client = clientRepository.findById(clientId).orElseThrow(
        () -> {
          throw new EntityNotFoundException("No client with id = " + clientId);
        });
    update.update(client);
    return client;
  }

}
