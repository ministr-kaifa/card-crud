package ru.zubkoff.sber.cardcrud.api.transport;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import ru.zubkoff.sber.cardcrud.core.domain.Client;

public record CreateClientDto(
  @Pattern(regexp = "\\d{10}", message = "passport number must be 10-digit number")
  @NotNull(message = "empty passport number")
  String passportNumber, 

  @Pattern(regexp = "^(?=.{1,100}$)[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$", message = "invalid name")
  @NotNull(message = "empty name")
  String name, 

  @Email(message = "invalid email")
  @NotBlank(message = "empty email")
  String email) {

  public Client toEntity() {
    return new Client(
      0L,
      passportNumber,
      name,
      email,
      List.of()
    );
  }

}
