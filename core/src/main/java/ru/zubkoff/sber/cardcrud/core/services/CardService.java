package ru.zubkoff.sber.cardcrud.core.services;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.zubkoff.sber.cardcrud.core.domain.Card;
import ru.zubkoff.sber.cardcrud.core.exceptions.EntityNotFoundException;
import ru.zubkoff.sber.cardcrud.core.persistence.CardRepository;

@Service
public class CardService {

  private static final Period CARD_VALIDITY_PERIOD = Period.ofYears(5);  

  private final CardRepository cardRepository;

  public CardService(CardRepository cardRepository) {
    this.cardRepository = cardRepository;
  }

  @Transactional
  public void createCard(Card card, long ownerClientId) {
    cardRepository.createCard(card, ownerClientId);
  }

  public List<Card> findAllCards(int page, int pageSize) {
    return cardRepository.findAll(PageRequest.of(page, pageSize)).toList();
  }

  public Card findCardById(Long cardId) {
    return cardRepository.findById(cardId).orElseThrow(
      () -> {throw new EntityNotFoundException("No card with id = " + cardId);});
  }

  public void deleteCard(Long cardId) {
    cardRepository.deleteById(cardId);
  }

  public List<Card> findCardsByValidToLessThanEqual(LocalDate validTo) {
    return cardRepository.findByValidToLessThanEqual(validTo);
  }

  /**
   * @param oldCard card to reissue
   * @return new reissued card
   */
  @Transactional
  public Card reissueCard(Card oldCard) {
    var cardWithBiggestCardNumber = cardRepository.findTopByOrderByCardNumberDesc().orElseThrow(() -> {
      throw new EntityNotFoundException("Cant find any persisted card");
    });
    var newCard = new Card();
    newCard.setCardNumber(String.valueOf(Long.parseLong(cardWithBiggestCardNumber.getCardNumber()) + 1));
    newCard.setValidFrom(LocalDate.now());
    newCard.setValidTo(LocalDate.now().plus(CARD_VALIDITY_PERIOD));
    cardRepository.createCard(newCard, oldCard.getOwner().getId());
    cardRepository.delete(oldCard);
    return newCard;
  }

  @Transactional
  public Card mergeById(Long mergeToCardId, Card mergeFrom) {
    var mergeTo = cardRepository.findById(mergeToCardId).orElseThrow(
      () -> {throw new EntityNotFoundException("No card with id = " + mergeToCardId);});
    merge(mergeFrom, mergeTo);
    return mergeTo;
  }

  private void merge(Card from, Card to) {
    if(from.getCardNumber() != null) {
      to.setCardNumber(from.getCardNumber());
    }
    if(from.getOwner() != null) {
      to.setOwner(from.getOwner());
    }
    if(from.getValidFrom() != null) {
      to.setValidFrom(from.getValidFrom());
    }
    if(from.getValidTo() != null) {
      to.setValidTo(from.getValidTo());
    }
  }
  
}
