package ru.zubkoff.sber.cardcrud.core.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.zubkoff.sber.cardcrud.core.domain.Card;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface CardRepository extends JpaRepository<Card, Long>, CustomizedCardRepository {
  List<Card> findByValidToLessThanEqual(LocalDate validTo);
  Optional<Card> findTopByOrderByCardNumberDesc();
}
