package ru.zubkoff.sber.cardcrud.api;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ru.zubkoff.sber.cardcrud.api.transport.CreateClientDto;
import ru.zubkoff.sber.cardcrud.api.transport.ReadClientDto;
import ru.zubkoff.sber.cardcrud.api.transport.UpdateClientDto;
import ru.zubkoff.sber.cardcrud.core.services.ClientService;

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


@RestController
@CrossOrigin(origins = {"*"})
public class ClientApiController {

  private static final int PAGE_SIZE = 40;

  private final ClientService clientService;

  public ClientApiController(ClientService clientService) {
    this.clientService = clientService;
  }

  @GetMapping("/api/clients")
  public List<ReadClientDto> getAllClients(@RequestParam(defaultValue = "0") int page) {
    return clientService.findAllClients(page, PAGE_SIZE).stream()
      .map(ReadClientDto::fromEntity)
      .toList();
  }
  
  @GetMapping("/api/clients/{clientId}")
  public ReadClientDto getClient(@PathVariable Long clientId) {
    return ReadClientDto.fromEntity(clientService.findClientById(clientId));
  }
  
  @PostMapping("/api/clients/add")
  @ResponseStatus(code = HttpStatus.CREATED)
  public ReadClientDto createClient(@RequestBody @Valid CreateClientDto createClientDto) {
    var client = createClientDto.toEntity();
    clientService.createClient(client);
    return ReadClientDto.fromEntity(client);
  }

  @DeleteMapping("/api/clients/{clientId}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteClient(@PathVariable Long clientId) {
    clientService.deleteClient(clientId);
  }

  @PatchMapping("/api/clients/{clientId}")
  public ReadClientDto updateClient(@PathVariable Long clientId, @RequestBody @Valid UpdateClientDto clientDto) {
    var client = clientDto.toEntity();
    return ReadClientDto.fromEntity(clientService.mergeById(clientId, client));
  }

}
