package ru.zubkoff.sber.cardcrud.emailsender.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@EnableRabbit
@Configuration
public class RabbitConfiguration {

  private final ApplicationProperties properties;

  public RabbitConfiguration(ApplicationProperties properties) {
   this.properties = properties; 
  }

  @Bean
  public ConnectionFactory connectionFactory() {
   return new CachingConnectionFactory(properties.getRabbitmqHost(), properties.getRabbitmqPort());
  }

  @Bean
  public AmqpAdmin amqpAdmin() {
   return new RabbitAdmin(connectionFactory());
  }

  @Bean
  public RabbitTemplate rabbitTemplate() {
   return new RabbitTemplate(connectionFactory());
  }

  @Bean
  public Queue notificationCardExpiredQueue() {
   return new Queue(properties.getRabbitmqEmailQueue());
  }

}
