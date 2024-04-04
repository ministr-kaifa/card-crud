package ru.zubkoff.sber.cardcrud.eventmanager.events.card.expired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ru.zubkoff.sber.cardcrud.core.services.CardService;

@Component
public class CardExpiredReissuer implements ApplicationListener<CardExpiredEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CardExpiredReissuer.class);

  private final CardService cardService;

  public CardExpiredReissuer(CardService cardService) {
    this.cardService = cardService;
  }

  @Override
  public void onApplicationEvent(CardExpiredEvent event) {
    var card = event.getCard();
    cardService.reissueCard(card);
    LOGGER.info("expired card reissued: client: %d; card: %d; ".formatted(card.getOwner().getId(), card.getId()));
  }

}
