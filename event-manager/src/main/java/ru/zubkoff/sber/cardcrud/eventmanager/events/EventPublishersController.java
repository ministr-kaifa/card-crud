package ru.zubkoff.sber.cardcrud.eventmanager.events;

import org.springframework.web.bind.annotation.RestController;

import ru.zubkoff.sber.cardcrud.eventmanager.events.card.expired.CardExpiredEventPublisher;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@CrossOrigin(origins = {"*"})
public class EventPublishersController {

  private final CardExpiredEventPublisher cardExpiredPublisher;

  public EventPublishersController(CardExpiredEventPublisher cardExpiredPublisher) {
    this.cardExpiredPublisher = cardExpiredPublisher;
  }

  @GetMapping("/api/events/card/expired/publish")
  public void getMethodName() {
    cardExpiredPublisher.publishAllExpiredCards();
  }

}
