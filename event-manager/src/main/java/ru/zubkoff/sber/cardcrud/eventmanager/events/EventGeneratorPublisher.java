package ru.zubkoff.sber.cardcrud.eventmanager.events;

import java.util.List;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

public abstract class EventGeneratorPublisher<T extends ApplicationEvent> {

  private final ApplicationEventPublisher appEventPublisher;

  protected EventGeneratorPublisher(ApplicationEventPublisher appEventPublisher) {
    this.appEventPublisher = appEventPublisher;
  }

  protected abstract List<T> generateEvents();

  public void generateAndPublish() {
    publishEvents(generateEvents());
  }

  private void publishEvents(List<T> events) {
    events.forEach(appEventPublisher::publishEvent);
  }

}
