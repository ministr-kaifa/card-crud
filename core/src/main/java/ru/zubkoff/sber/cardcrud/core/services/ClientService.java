package ru.zubkoff.sber.cardcrud.core.services;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.zubkoff.sber.cardcrud.core.domain.Client;
import ru.zubkoff.sber.cardcrud.core.exceptions.EntityNotFoundException;
import ru.zubkoff.sber.cardcrud.core.persistence.ClientRepository;

@Service
public class ClientService {

  private final ClientRepository clientRepository;

  public ClientService(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public Client findClientById(Long clientId) {
    return clientRepository.findById(clientId).orElseThrow(
      () -> {throw new EntityNotFoundException("No client with id = " + clientId);});
  }
  
  public Client findClientWithCardsById(Long clientId) {
    return clientRepository.findClientWithCardsById(clientId).orElseThrow(
      () -> {throw new EntityNotFoundException("No client with id = " + clientId);});
  }

  public void deleteClient(Long clientId) {
    clientRepository.deleteById(clientId);
  }

  @Transactional
  public List<Client> findAllClients(int page, int pageSize) {
    return clientRepository.findAll(PageRequest.of(page, pageSize)).toList();
  }

  @Transactional
  public void createClient(Client client) {
    clientRepository.createClient(client);
  }

  @Transactional
  public Client mergeById(Long mergeToClientId, Client mergeFrom) {
    var mergeTo = clientRepository.findById(mergeToClientId).orElseThrow(
      () -> {throw new EntityNotFoundException("No client with id = " + mergeToClientId);});
    merge(mergeFrom, mergeTo);
    return mergeTo;
  }

  private void merge(Client from, Client to) {
    if(from.getPassportNumber() != null) {
      to.setPassportNumber(from.getPassportNumber());
    }
    if(from.getEmail() != null) {
      to.setEmail(from.getEmail());
    }
    if(from.getName() != null) {
      to.setName(from.getName());
    }
    if(from.getCards() != null) {
      to.setCards(from.getCards());
    }
  }

}
