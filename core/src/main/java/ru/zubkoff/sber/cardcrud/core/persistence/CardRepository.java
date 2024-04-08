package ru.zubkoff.sber.cardcrud.core.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.zubkoff.sber.cardcrud.core.domain.card.Card;

public interface CardRepository extends JpaRepository<Card, Long>, CustomizedCardRepository {
  @Query("select c from Card c where c.serviceLife.validTo < :validTo")
  List<Card> findByCardCardServiceLifeValidToLessThanEqual(LocalDate validTo);

  Optional<Card> findTopByOrderByCardNumberDesc();
}
