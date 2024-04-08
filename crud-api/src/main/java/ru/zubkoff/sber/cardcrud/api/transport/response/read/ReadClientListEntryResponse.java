package ru.zubkoff.sber.cardcrud.api.transport.response.read;

import ru.zubkoff.sber.cardcrud.core.domain.client.Client;

public record ReadClientListEntryResponse(long id, String name) {
  public static ReadClientListEntryResponse fromEntity(Client entity) {
    return new ReadClientListEntryResponse(
        entity.getId(),
        entity.getName().value());
  }
}
