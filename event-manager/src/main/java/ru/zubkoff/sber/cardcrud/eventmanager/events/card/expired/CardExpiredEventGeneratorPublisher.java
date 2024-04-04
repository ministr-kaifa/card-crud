package ru.zubkoff.sber.cardcrud.eventmanager.events.card.expired;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.zubkoff.sber.cardcrud.core.services.CardService;
import ru.zubkoff.sber.cardcrud.eventmanager.events.EventGeneratorPublisher;

@Component
public class CardExpiredEventGeneratorPublisher extends EventGeneratorPublisher<CardExpiredEvent> {

  private final CardService cardService;

  public CardExpiredEventGeneratorPublisher(CardService cardService, ApplicationEventPublisher eventPublisher) {
    super(eventPublisher);
    this.cardService = cardService;
  }

  @Override
  @Scheduled(cron = "0 0 12 * * ?")
  public void generateAndPublish() {
    super.generateAndPublish();
  }

  @Override
  protected List<CardExpiredEvent> generateEvents() {
    return cardService.findCardsByValidToLessThanEqual(LocalDate.now()).stream()
        .map(expiredCard -> new CardExpiredEvent(this, expiredCard.getOwner(), expiredCard))
        .toList();
  }

}
