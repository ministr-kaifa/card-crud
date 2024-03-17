package ru.zubkoff.sber.cardcrud.eventmanager.events.card.expired;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ru.zubkoff.sber.cardcrud.core.domain.Card;
import ru.zubkoff.sber.cardcrud.core.services.CardService;

@Component
public class CardExpiredEventPublisher {

  private static final Logger LOGGER = LoggerFactory.getLogger(CardExpiredEventPublisher.class);
  private final CardService cardService;
  private ApplicationEventPublisher eventPublisher;

  public CardExpiredEventPublisher(CardService cardService, ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
    this.cardService = cardService;
  }

  @Transactional
  @Scheduled(cron = "0 0 12 * * ?")
  public void publishAllExpiredCards() {
    LOGGER.info("expired cards check");
    List<Card> expiredCard = cardService.findCardsByValidToLessThanEqual(LocalDate.now());
    expiredCard.forEach(
      card -> {
        LOGGER.info("expired card: client: " + card.getOwner().getId() + "; card: " + card.getId() + ";");
        try {
          eventPublisher.publishEvent(new CardExpiredEvent(this, card.getOwner(), card));
        } catch (Exception e) {
          LOGGER.error("CardExpiredEvent listener throwed exception while event publishing", e);
        }
      }
    );
  }

}
