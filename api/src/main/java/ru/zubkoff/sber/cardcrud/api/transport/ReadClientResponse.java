package ru.zubkoff.sber.cardcrud.api.transport;

import java.util.List;

import ru.zubkoff.sber.cardcrud.core.domain.Client;

public record ReadClientResponse(Long id, String name, String passportNumber, String email,
    List<ReadCardResponse> cards) {
  public static ReadClientResponse fromEntity(Client entity) {
    return new ReadClientResponse(
        entity.getId(),
        entity.getName(),
        entity.getPassportNumber(),
        entity.getEmail(),
        entity.getCards().stream()
            .map(ReadCardResponse::fromEntity)
            .toList());
  }
}
