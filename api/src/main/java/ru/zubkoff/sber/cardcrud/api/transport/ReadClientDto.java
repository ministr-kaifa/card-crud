package ru.zubkoff.sber.cardcrud.api.transport;

import java.util.List;

import ru.zubkoff.sber.cardcrud.core.domain.Client;

public record ReadClientDto(Long id, String name, String passportNumber, String email, List<ReadCardDto> cards) {
  public static ReadClientDto fromEntity(Client entity) {
    return new ReadClientDto(
    entity.getId(),
    entity.getName(),
    entity.getPassportNumber(),
    entity.getEmail(),
    entity.getCards().stream()
      .map(ReadCardDto::fromEntity)
      .toList()
    );
  }
}
