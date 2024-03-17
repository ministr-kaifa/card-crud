package ru.zubkoff.sber.cardcrud.api.transport;

import java.time.LocalDate;

import ru.zubkoff.sber.cardcrud.core.domain.Card;

public record ReadCardDto(Long id, Long ownerId, String cardNumber, LocalDate validFrom, LocalDate validTo) {
  public static ReadCardDto fromEntity(Card entity) {
    return new ReadCardDto(
    entity.getId(),
    entity.getOwner().getId(),
    entity.getCardNumber(),
    entity.getValidFrom(),
    entity.getValidTo());
  }
}
