package ru.zubkoff.sber.cardcrud.core.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.zubkoff.sber.cardcrud.core.domain.Client;

public interface ClientRepository extends JpaRepository<Client, Long>, CustomizedClientRepository {

  @Query("""
    select c 
    from Client c 
    left join fetch c.cards
    where c.id = :clientId
  """)
  Optional<Client> findClientWithCardsById(Long clientId);

}

