package ru.zubkoff.sber.cardcrud.eventmanager.events.card.expired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ru.zubkoff.sber.cardcrud.core.services.CardService;

@Component
public class ExpiredCardReissuer implements ApplicationListener<CardExpiredEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredCardReissuer.class);

  private final CardService cardService;

  public ExpiredCardReissuer(CardService cardService) {
    this.cardService = cardService;
  }

  @Override
  @Transactional
  public void onApplicationEvent(CardExpiredEvent event) {
    var card = event.getCard();
    cardService.reissueCard(card);
    LOGGER.info("expired card reissued: client: " + card.getOwner().getId() + "; card: " + card.getId() + ";");
  }
  
}
