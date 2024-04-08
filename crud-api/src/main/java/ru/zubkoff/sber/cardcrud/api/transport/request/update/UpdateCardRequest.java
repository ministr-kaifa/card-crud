package ru.zubkoff.sber.cardcrud.api.transport.request.update;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardNumber;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardUpdate;

public record UpdateCardRequest(
    @Min(0) Long newOwnerId,
    @Pattern(regexp = CardNumber.VALIDATION_REGEX, message = "card number must be 16-digit number") String cardNumber,
    @JsonFormat(pattern = "yyyy-M-d") LocalDate validFrom,
    @JsonFormat(pattern = "yyyy-M-d") LocalDate validTo) {

  @AssertTrue(message = "valid from must be before valid to")
  public boolean isValidFromBeforeValidTo() {
    return (validFrom == null || validTo == null) || validFrom.isBefore(validTo);
  }

  public CardUpdate toCardUpdate() {
    return new CardUpdate(
        Optional.ofNullable(newOwnerId),
        Optional.ofNullable(cardNumber).map(CardNumber::new),
        Optional.ofNullable(validFrom),
        Optional.ofNullable(validTo));
  }

}
