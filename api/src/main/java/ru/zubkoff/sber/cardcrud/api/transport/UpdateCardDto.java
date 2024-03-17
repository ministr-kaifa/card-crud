package ru.zubkoff.sber.cardcrud.api.transport;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import ru.zubkoff.sber.cardcrud.core.domain.Card;
import ru.zubkoff.sber.cardcrud.core.domain.Client;

public record UpdateCardDto(
  @Min(0)
  Long ownerId, 

  @Pattern(regexp = "\\d{16}", message = "card number must be 16-digit number")
  String cardNumber, 

  @JsonFormat(pattern="yyyy-M-d")
  LocalDate validFrom, 

  @JsonFormat(pattern="yyyy-M-d")
  LocalDate validTo) {
    
  @AssertTrue(message = "valid from must be before valid to")
  public boolean isValidFromBeforeValidTo() {
    return (validFrom == null || validTo == null) || validFrom.isBefore(validTo);
  }

  public Card toEntity() {
    var client = ownerId != null? new Client(ownerId, null, null, null, null) : null;
    return new Card(0L, client, cardNumber, validFrom, validTo);
  }

}
