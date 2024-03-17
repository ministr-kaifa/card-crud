package ru.zubkoff.sber.cardcrud.emailsender.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Component
public class EmailService {

  private final JavaMailSender javaMailSender;
  private final ObjectMapper objectMapper = new ObjectMapper();
  
  public EmailService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  @RabbitListener(queues = "${rabbitmq-email-queue}")
  public void sendMail(String message) throws JsonProcessingException, MessagingException {
    var emailInfo = objectMapper.readValue(message, Email.class);
    var email = javaMailSender.createMimeMessage();
    var helper = new MimeMessageHelper(email);
    helper.setFrom(emailInfo.sender());
    helper.setTo(emailInfo.recipient());
    helper.setText(emailInfo.content(), true);
    javaMailSender.send(email);
  }

}
