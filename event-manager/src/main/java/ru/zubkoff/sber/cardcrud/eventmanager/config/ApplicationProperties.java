package ru.zubkoff.sber.cardcrud.eventmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "card-crud")
public class ApplicationProperties {
  
  private String rabbitmqEmailQueue;
  private String rabbitmqHost;
  private String cardExpiredNotificationEmailSender;
  private int rabbitmqPort;

  public int getRabbitmqPort() {
    return rabbitmqPort;
  }

  public String getRabbitmqHost() {
    return rabbitmqHost;
  }

  public String getRabbitmqEmailQueue() {
    return rabbitmqEmailQueue;
  }
  
  public String getCardExpiredNotificationEmailSender() {
    return cardExpiredNotificationEmailSender;
  }
  
  public void setCardExpiredNotificationEmailSender(String cardExpiredNotificationEmailSender) {
    this.cardExpiredNotificationEmailSender = cardExpiredNotificationEmailSender;
  }

  public void setRabbitmqEmailQueue(String rabbitmqEmailQueue) {
    this.rabbitmqEmailQueue = rabbitmqEmailQueue;
  }

  public void setRabbitmqHost(String rabbitmqHost) {
    this.rabbitmqHost = rabbitmqHost;
  }
  
  public void setRabbitmqPort(int rabbitmqPort) {
    this.rabbitmqPort = rabbitmqPort;
  }

}
