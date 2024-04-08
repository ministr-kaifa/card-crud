package ru.zubkoff.sber.cardcrud.api.transport.request.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import ru.zubkoff.sber.cardcrud.core.domain.client.ClientCreation;
import ru.zubkoff.sber.cardcrud.core.domain.client.EmailAddress;
import ru.zubkoff.sber.cardcrud.core.domain.client.Name;
import ru.zubkoff.sber.cardcrud.core.domain.client.PassportNumber;

public record CreateClientRequest(
    @NotNull(message = "empty passport number") @Pattern(regexp = PassportNumber.VALIDATION_REGEX, message = "passport number must be 10-digit number") String passportNumber,
    @NotNull(message = "empty name") @Pattern(regexp = Name.VALIDATION_REGEX, message = "invalid name") String name,
    @NotBlank(message = "empty email") @Pattern(regexp = EmailAddress.VALIDATION_REGEX, message = "invalid email") String email) {

  public ClientCreation toClientCreation() {
    return new ClientCreation(new PassportNumber(passportNumber), new Name(name), new EmailAddress(email));
  }

}
