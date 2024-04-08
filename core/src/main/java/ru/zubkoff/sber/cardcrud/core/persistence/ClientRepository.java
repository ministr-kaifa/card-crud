package ru.zubkoff.sber.cardcrud.core.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.zubkoff.sber.cardcrud.core.domain.client.Client;

public interface ClientRepository extends JpaRepository<Client, Long>, CustomizedClientRepository {

  @EntityGraph("client-with-cards-graph")
  Optional<Client> findClientWithCardsById(Long clientId);

}
