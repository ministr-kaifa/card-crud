package ru.zubkoff.sber.cardcrud.api.transport.response.read;

import java.time.LocalDate;

import ru.zubkoff.sber.cardcrud.core.domain.card.Card;

public record ReadCardListEntryResponse(long id, String cardNumber, LocalDate validFrom,
    LocalDate validTo) {
  public static ReadCardListEntryResponse fromEntity(Card entity) {
    return new ReadCardListEntryResponse(
        entity.getId(),
        entity.getCardNumber().value(),
        entity.getServiceLife().validFrom(),
        entity.getServiceLife().validTo());
  }
}
