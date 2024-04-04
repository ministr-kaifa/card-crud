package ru.zubkoff.sber.cardcrud.eventmanager.events.card.expired.email;

import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.zubkoff.sber.cardcrud.eventmanager.config.ApplicationProperties;
import ru.zubkoff.sber.cardcrud.eventmanager.events.card.expired.CardExpiredEvent;

@Component
public class CardExpiredEmailNotificationAmqpPublisher implements ApplicationListener<CardExpiredEvent> {

  private static final Path CARD_EXPIRED_EMAIL_TEMPLATE_PATH = Path.of("email/cardExpired");
  private static final Logger LOGGER = LoggerFactory.getLogger(CardExpiredEmailNotificationAmqpPublisher.class);

  private final ApplicationProperties properties;

  private final TemplateEngine templateEngine;
  private final AmqpTemplate amqpTemplate;

  public CardExpiredEmailNotificationAmqpPublisher(AmqpTemplate amqpTemplate, TemplateEngine templateEngine,
      ApplicationProperties properties) {
    this.amqpTemplate = amqpTemplate;
    this.templateEngine = templateEngine;
    this.properties = properties;
  }

  @Override
  public void onApplicationEvent(CardExpiredEvent event) {
    var templateContext = new Context();
    templateContext.setVariables(
        Map.of(
            "clientName", event.getClient().getName(),
            "expiredCardNumber", event.getCard().getCardNumber()));

    var emailContent = templateEngine.process(
        CARD_EXPIRED_EMAIL_TEMPLATE_PATH.toString(),
        templateContext);

    var email = new Email(
        emailContent,
        properties.getCardExpiredNotificationEmailSender(),
        event.getClient().getEmail());

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      amqpTemplate.convertAndSend(
          properties.getRabbitmqEmailQueue(),
          objectMapper.writeValueAsString(email));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    LOGGER.info("expired email notification sent: client: %d; card: %d;".formatted(event.getClient().getId(),
        event.getCard().getId()));
  }

}
