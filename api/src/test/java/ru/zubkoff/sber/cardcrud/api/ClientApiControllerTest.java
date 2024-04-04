package ru.zubkoff.sber.cardcrud.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.zubkoff.sber.cardcrud.api.exceptions.ControllerExceptionHandler;
import ru.zubkoff.sber.cardcrud.api.transport.CreateClientRequest;
import ru.zubkoff.sber.cardcrud.api.transport.ReadClientResponse;
import ru.zubkoff.sber.cardcrud.api.transport.UpdateClientRequest;
import ru.zubkoff.sber.cardcrud.core.domain.Client;
import ru.zubkoff.sber.cardcrud.core.exceptions.EntityNotFoundException;
import ru.zubkoff.sber.cardcrud.core.exceptions.NonUniqueValue;
import ru.zubkoff.sber.cardcrud.core.services.ClientService;

@ContextConfiguration(classes = { ControllerExceptionHandler.class, ClientApiController.class })
@WebMvcTest(ClientApiController.class)
class ClientApiControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  ClientService clientService;

  @Autowired
  ObjectMapper objectMapper;

  Client client = new Client(1L, "1234567890", "Иванов Иван Иванович", "ivanov@example.com", List.of());
  CreateClientRequest createClient = new CreateClientRequest(client.getPassportNumber(), client.getName(),
      client.getEmail());

  String json(Object obj) throws JsonProcessingException {
    return objectMapper.writeValueAsString(obj);
  }

  ResultMatcher[] isSameClient() {
    return new ResultMatcher[] {
        jsonPath("$.id").value(client.getId()),
        jsonPath("$.name").value(client.getName()),
        jsonPath("$.passportNumber").value(client.getPassportNumber()),
        jsonPath("$.email").value(client.getEmail()),
        jsonPath("$.cards").isArray()
    };
  }

  @Test
  void givenSingleClient_whenGetAllClients_thenReturnSingleClient() throws Exception {
    given(clientService.findAllClients(0, 40))
        .willReturn(List.of(client));

    mockMvc.perform(get("/api/clients"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(client.getId()))
        .andExpect(jsonPath("$[0].name").value(client.getName()))
        .andExpect(jsonPath("$[0].passportNumber").value(client.getPassportNumber()))
        .andExpect(jsonPath("$[0].email").value(client.getEmail()))
        .andExpect(jsonPath("$[0].cards").isArray());
  }

  @Test
  void givenSpecificClient_whenGetSpecificClient_thenReturnSpecificClient() throws Exception {
    var targetId = client.getId();
    given(clientService.findClientById(targetId))
        .willReturn(client);

    mockMvc.perform(get("/api/clients/" + targetId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(isSameClient());
  }

  @Test
  void deletionIsRepeatable() throws Exception {
    mockMvc.perform(delete("/api/clients/1"))
        .andExpect(status().isNoContent());

    mockMvc.perform(delete("/api/clients/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void whenUpdateClient_thenReturnUpdatedClient() throws Exception {
    UpdateClientRequest updateClient = new UpdateClientRequest(client.getPassportNumber(), client.getName(),
        client.getEmail());
    var targetId = client.getId();
    given(clientService.mergeById(anyLong(), any()))
        .willReturn(client);

    mockMvc.perform(patch("/api/clients/" + targetId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json(updateClient)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(isSameClient());
  }

  @Test
  void whenCreateClient_thenReturnCreatedClient() throws Exception {
    ReadClientResponse readClient = new ReadClientResponse(client.getId(), client.getName(), client.getPassportNumber(),
        client.getEmail(), List.of());
    try (var readClientDto = mockStatic(ReadClientResponse.class)) {
      readClientDto.when(() -> ReadClientResponse.fromEntity(any(Client.class)))
          .thenReturn(readClient);

      mockMvc.perform(post("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json(createClient)))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpectAll(isSameClient());
    }

  }

  @Test
  void givenNoClientId_whenGetClientByNoClientId_thenNotFound() throws Exception {
    var noClientId = 1L;
    given(clientService.findClientById(noClientId))
        .willThrow(new EntityNotFoundException("entity not found"));

    mockMvc.perform(get("/api/clients/{clientId}", noClientId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
        .andExpect(result -> assertEquals("entity not found", result.getResolvedException().getMessage()));
  }

  @Test
  void givenNonUniqueClient_whenCreatingNonUniqueClient_thenConflict() throws Exception {
    doThrow(new NonUniqueValue("non unique value"))
        .when(clientService).createClient(any());

    mockMvc.perform(post("/api/clients/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json(createClient)))
        .andExpect(status().isConflict())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof NonUniqueValue))
        .andExpect(result -> assertEquals("non unique value", result.getResolvedException().getMessage()));
  }

  @Test
  void givenInvalidClientInfos_whenCreatingClients_thenBadRequest() throws Exception {
    var validPassportNumber = client.getPassportNumber();
    var validName = client.getName();
    var validEmail = client.getEmail();
    var invalidDummies = List.of(
        new CreateClientRequest(null, validName, validEmail),
        new CreateClientRequest("", validName, validEmail),
        new CreateClientRequest(" ", validName, validEmail),
        new CreateClientRequest("123", validName, validEmail),
        new CreateClientRequest(validPassportNumber + "123", validName, validEmail),

        new CreateClientRequest(validPassportNumber, null, validEmail),
        new CreateClientRequest(validPassportNumber, "", validEmail),
        new CreateClientRequest(validPassportNumber, "43прар5", validEmail),
        new CreateClientRequest(validPassportNumber, " ", validEmail),

        new CreateClientRequest(validPassportNumber, validName, null),
        new CreateClientRequest(validPassportNumber, validName, ""),
        new CreateClientRequest(validPassportNumber, validName, "@."),
        new CreateClientRequest(validPassportNumber, validName, "@ru.ru"),
        new CreateClientRequest(validPassportNumber, validName, "abc@.ru"));

    for (var invalidDummy : invalidDummies) {
      mockMvc.perform(post("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json(invalidDummy)))
          .andExpect(status().isBadRequest())
          .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }
  }
}
