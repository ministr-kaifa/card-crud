package ru.zubkoff.sber.cardcrud.core.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import ru.zubkoff.sber.cardcrud.core.domain.card.Card;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardCreation;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardNumber;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardUpdate;
import ru.zubkoff.sber.cardcrud.core.persistence.CardRepository;
import ru.zubkoff.sber.cardcrud.core.persistence.ClientRepository;

@Service
public class CardService {

  private final CardRepository cardRepository;
  private final ClientRepository clientRepository;

  public CardService(CardRepository cardRepository, ClientRepository clientRepository) {
    this.cardRepository = cardRepository;
    this.clientRepository = clientRepository;
  }

  @Transactional
  public Card createCard(CardCreation newCard, long ownerId) {
    var client = clientRepository.findById(ownerId).orElseThrow(
        () -> {
          throw new EntityNotFoundException("No client with id = " + ownerId);
        });
    newCard.fetchOwner(client);
    return cardRepository.save(newCard.toEntity());
  }

  public List<Card> findAllCards(int page, int pageSize) {
    return cardRepository.findAll(PageRequest.of(page, pageSize)).toList();
  }

  public Card findCardById(long cardId) {
    return cardRepository.findById(cardId).orElseThrow(
        () -> {
          throw new EntityNotFoundException("No card with id = " + cardId);
        });
  }

  public void deleteCard(long cardId) {
    cardRepository.deleteById(cardId);
  }

  public List<Card> findCardsByValidToLessThanEqual(LocalDate validTo) {
    return cardRepository.findByCardCardServiceLifeValidToLessThanEqual(validTo);
  }

  /**
   * @param oldCard card to reissue
   * @return new reissued card
   */
  @Transactional
  public Card reissueCard(Card oldCard) {
    var biggestCardNumber = cardRepository.findTopByOrderByCardNumberDesc().orElseThrow(() -> {
      throw new EntityNotFoundException("Cant find any persisted card");
    }).getCardNumber().value();
    var newCard = oldCard.reissued(
        new CardNumber("%016d".formatted(Long.parseLong(biggestCardNumber) + 1)),
        LocalDate.now());
    cardRepository.save(newCard);
    cardRepository.delete(oldCard);
    return newCard;
  }

  @Transactional
  public Card updateById(long cardId, CardUpdate update) {
    var card = cardRepository.findById(cardId).orElseThrow(
        () -> {
          throw new EntityNotFoundException("No card with id = " + cardId);
        });
    update.getNewOwnerId()
        .ifPresent(ownerId -> update.fetchOwner(
            clientRepository.findById(ownerId).orElseThrow(
                () -> {
                  throw new EntityNotFoundException("No client with id = " + ownerId);
                })));
    update.apply(card);
    return card;
  }

}
