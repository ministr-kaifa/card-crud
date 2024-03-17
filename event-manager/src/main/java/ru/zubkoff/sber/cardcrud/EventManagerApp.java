package ru.zubkoff.sber.cardcrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EventManagerApp {
  public static void main(String[] args) {
    SpringApplication.run(EventManagerApp.class, args);
  }

}
