package ru.zubkoff.sber.cardcrud.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ru.zubkoff.sber.cardcrud.api.transport.CreateClientRequest;
import ru.zubkoff.sber.cardcrud.api.transport.ReadClientListEntryResponse;
import ru.zubkoff.sber.cardcrud.api.transport.ReadClientResponse;
import ru.zubkoff.sber.cardcrud.api.transport.UpdateClientRequest;
import ru.zubkoff.sber.cardcrud.core.services.ClientService;

@RestController
@CrossOrigin(origins = { "*" })
public class ClientApiController {

  private static final int PAGE_SIZE = 40;

  private final ClientService clientService;

  public ClientApiController(ClientService clientService) {
    this.clientService = clientService;
  }

  @GetMapping("/api/clients")
  public List<ReadClientListEntryResponse> getAllClients(@RequestParam(defaultValue = "0") int page) {
    return clientService.findAllClients(page, PAGE_SIZE).stream()
        .map(ReadClientListEntryResponse::fromEntity)
        .toList();
  }

  @GetMapping("/api/clients/{clientId}")
  public ReadClientResponse getClient(@PathVariable Long clientId) {
    return ReadClientResponse.fromEntity(clientService.findClientById(clientId));
  }

  @PostMapping("/api/clients")
  @ResponseStatus(code = HttpStatus.CREATED)
  public ReadClientResponse createClient(@RequestBody @Valid CreateClientRequest createClientDto) {
    var client = createClientDto.toEntity();
    clientService.createClient(client);
    return ReadClientResponse.fromEntity(client);
  }

  @DeleteMapping("/api/clients/{clientId}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteClient(@PathVariable Long clientId) {
    clientService.deleteClient(clientId);
  }

  @PatchMapping("/api/clients/{clientId}")
  public ReadClientResponse updateClient(@PathVariable Long clientId,
      @RequestBody @Valid UpdateClientRequest clientDto) {
    var client = clientDto.toEntity();
    return ReadClientResponse.fromEntity(clientService.mergeById(clientId, client));
  }

}
