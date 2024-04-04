package ru.zubkoff.sber.cardcrud.api.transport;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import ru.zubkoff.sber.cardcrud.core.domain.Client;

public record UpdateClientRequest(

    @Pattern(regexp = "\\d{10}", message = "passport number must be 10-digit number") String passportNumber,

    @Pattern(regexp = "^(?=.{1,100}$)[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$", message = "invalid name") String name,

    @Email(message = "invalid email") String email) {

  public Client toEntity() {
    return new Client(
        null,
        passportNumber,
        name,
        email,
        null);
  }

}
