package ru.zubkoff.sber.cardcrud.api.transport.response.read;

import java.util.List;

import ru.zubkoff.sber.cardcrud.core.domain.client.Client;

public record ReadClientResponse(Long id, String name, String passportNumber, String email,
    List<ReadCardListEntryResponse> cards) {
  public static ReadClientResponse fromEntity(Client entity) {
    return new ReadClientResponse(
        entity.getId(),
        entity.getName().value(),
        entity.getPassportNumber().value(),
        entity.getEmail().value(),
        entity.getCards().stream()
            .map(ReadCardListEntryResponse::fromEntity)
            .toList());
  }
}
