package ru.zubkoff.sber.cardcrud.api.transport.request.create;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardCreation;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardNumber;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardServiceLife;

public record CreateCardRequest(
    @Min(0) @NotNull Long ownerId,
    @NotNull(message = "empty card number") @Pattern(regexp = CardNumber.VALIDATION_REGEX, message = "card number must be 16-digit number") String cardNumber,
    @NotNull(message = "empty valid from") @JsonFormat(pattern = "yyyy-M-d") LocalDate validFrom,
    @NotNull(message = "empty valid to") @JsonFormat(pattern = "yyyy-M-d") LocalDate validTo) {

  @AssertTrue(message = "valid from must be before valid to")
  public boolean isValidFromBeforeValidTo() {
    return (validFrom != null && validTo != null) && validFrom.isBefore(validTo);
  }

  public CardCreation toCardCreation() {
    return new CardCreation(new CardNumber(cardNumber), new CardServiceLife(validFrom, validTo));
  }

}
