package ru.zubkoff.sber.cardcrud.api.transport;

import ru.zubkoff.sber.cardcrud.core.domain.Client;

public record ReadClientListEntryResponse(Long id, String name) {
  public static ReadClientListEntryResponse fromEntity(Client entity) {
    return new ReadClientListEntryResponse(
        entity.getId(),
        entity.getName());
  }
}
