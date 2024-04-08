package ru.zubkoff.sber.cardcrud.api.transport.response.read;

import java.time.LocalDate;

import ru.zubkoff.sber.cardcrud.core.domain.card.Card;

public record ReadCardResponse(long id, Long ownerId, String cardNumber, LocalDate validFrom, LocalDate validTo) {
  public static ReadCardResponse fromEntity(Card entity) {
    return new ReadCardResponse(
        entity.getId(),
        entity.getOwner().getId(),
        entity.getCardNumber().value(),
        entity.getServiceLife().validFrom(),
        entity.getServiceLife().validTo());
  }
}
