package ru.zubkoff.sber.cardcrud.eventmanager.events;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = { "*" })
public class EventPublishersController {

  private final Map<String, EventGeneratorPublisher<?>> cardExpiredPublisher;

  public EventPublishersController(Map<String, EventGeneratorPublisher<?>> generatorPublishers) {
    this.cardExpiredPublisher = generatorPublishers;
  }

  @PostMapping("/api/events/generatorPublishers/{generatorPublisherName}/tasks")
  public void getMethodName(@PathVariable String generatorPublisherName) {
    var generatorPublisher = cardExpiredPublisher.get(generatorPublisherName);
    if (generatorPublisher == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    generatorPublisher.generateAndPublish();
  }

}
