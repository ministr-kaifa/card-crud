package ru.zubkoff.sber.cardcrud.api.transport;

import java.time.LocalDate;

import ru.zubkoff.sber.cardcrud.core.domain.Card;

public record ReadCardResponse(Long id, Long ownerId, String cardNumber, LocalDate validFrom, LocalDate validTo) {
  public static ReadCardResponse fromEntity(Card entity) {
    return new ReadCardResponse(
        entity.getId(),
        entity.getOwner().getId(),
        entity.getCardNumber(),
        entity.getValidFrom(),
        entity.getValidTo());
  }
}
