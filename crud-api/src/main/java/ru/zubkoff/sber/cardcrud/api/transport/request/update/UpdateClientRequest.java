package ru.zubkoff.sber.cardcrud.api.transport.request.update;

import java.util.Optional;

import jakarta.validation.constraints.Pattern;
import ru.zubkoff.sber.cardcrud.core.domain.client.ClientUpdate;
import ru.zubkoff.sber.cardcrud.core.domain.client.EmailAddress;
import ru.zubkoff.sber.cardcrud.core.domain.client.Name;
import ru.zubkoff.sber.cardcrud.core.domain.client.PassportNumber;

public record UpdateClientRequest(
    @Pattern(regexp = PassportNumber.VALIDATION_REGEX, message = "passport number must be 10-digit number") String passportNumber,
    @Pattern(regexp = Name.VALIDATION_REGEX, message = "invalid name") String name,
    @Pattern(regexp = EmailAddress.VALIDATION_REGEX, message = "invalid email") String email) {

  public ClientUpdate toClientUpdate() {
    return new ClientUpdate(
        Optional.ofNullable(passportNumber).map(PassportNumber::new),
        Optional.ofNullable(name).map(Name::new),
        Optional.ofNullable(email).map(EmailAddress::new));
  }

}
