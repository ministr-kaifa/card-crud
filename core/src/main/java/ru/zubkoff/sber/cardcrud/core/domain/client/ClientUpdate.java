package ru.zubkoff.sber.cardcrud.core.domain.client;

import java.util.Optional;

public record ClientUpdate(
    Optional<PassportNumber> passportNumber,
    Optional<Name> name,
    Optional<EmailAddress> email) {

  public void update(Client client) {
    email.ifPresent(client::setEmail);
    name.ifPresent(client::setName);
    passportNumber.ifPresent(client::setPassportNumber);
  }
}
