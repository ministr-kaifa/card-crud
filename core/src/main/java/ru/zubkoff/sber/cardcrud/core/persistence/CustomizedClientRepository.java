package ru.zubkoff.sber.cardcrud.core.persistence;

import ru.zubkoff.sber.cardcrud.core.domain.client.Client;

public interface CustomizedClientRepository {
  public void createClient(Client client);
}
