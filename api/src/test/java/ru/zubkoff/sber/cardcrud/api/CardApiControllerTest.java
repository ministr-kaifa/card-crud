package ru.zubkoff.sber.cardcrud.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import ru.zubkoff.sber.cardcrud.api.transport.CreateCardDto;
import ru.zubkoff.sber.cardcrud.api.transport.ReadCardDto;
import ru.zubkoff.sber.cardcrud.api.transport.UpdateCardDto;
import ru.zubkoff.sber.cardcrud.core.domain.Card;
import ru.zubkoff.sber.cardcrud.core.domain.Client;
import ru.zubkoff.sber.cardcrud.core.services.CardService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardApiController.class)
class CardApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CardService cardService;

  @Autowired
  private ObjectMapper objectMapper;

  Client client = new Client(1L, "1234567890", "Иванов Иван Иванович", "ivanov@example.com", List.of());
  Card card = new Card(1L, client, "1111222233334444", LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1));

  String json(Object obj) throws JsonProcessingException {
    return objectMapper.writeValueAsString(obj);
  }

  ResultMatcher[] isSameCard() {
    return new ResultMatcher[] {
      jsonPath("$.id").value(card.getId()),
      jsonPath("$.cardNumber").value(card.getCardNumber()),
      jsonPath("$.ownerId").value(card.getOwner().getId()),
      jsonPath("$.validFrom").value(card.getValidFrom().toString()),
      jsonPath("$.validTo").value(card.getValidTo().toString())
    };
  }

  @Test
  void givenSingleCard_whenGetAllCards_thenReturnSingleCard() throws Exception {
    given(cardService.findAllCards(0, 40))
      .willReturn(List.of(card));

    mockMvc.perform(get("/api/cards"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].id").value(card.getId()))
      .andExpect(jsonPath("$[0].cardNumber").value(card.getCardNumber()))
      .andExpect(jsonPath("$[0].ownerId").value(card.getOwner().getId()))
      .andExpect(jsonPath("$[0].validFrom").value(card.getValidFrom().toString()))
      .andExpect(jsonPath("$[0].validTo").value(card.getValidTo().toString()));
  }

  @Test
  void givenSpecificCard_whenGetSpecificCard_thenReturnSpecificCard() throws Exception {
    var targetId = 1L;
    given(cardService.findCardById(targetId))
      .willReturn(card);

    mockMvc.perform(get("/api/cards/" + targetId))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpectAll(isSameCard());
  }

  @Test
  void deletionIsRepeatable() throws Exception {
    mockMvc.perform(delete("/api/cards/1"))
      .andExpect(status().isNoContent());

    mockMvc.perform(delete("/api/cards/1"))
      .andExpect(status().isNoContent());
  }

  @Test
  void whenUpdateCard_thenReturnUpdatedCard() throws Exception {
    var targetId = 1L;
    UpdateCardDto updateCard = new UpdateCardDto(card.getOwner().getId(), card.getCardNumber(), card.getValidFrom(), card.getValidTo());
    given(cardService.mergeById(eq(targetId), any()))
      .willReturn(card);

    mockMvc.perform(patch("/api/cards/" + targetId)
      .contentType(MediaType.APPLICATION_JSON)
      .content(json(updateCard)))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpectAll(isSameCard());
  }

  @Test
  void whenCreateCard_thenReturnCreatedCard() throws Exception {
    CreateCardDto createCard = new CreateCardDto(card.getOwner().getId(), card.getCardNumber(), card.getValidFrom(), card.getValidTo());
    ReadCardDto readCard = new ReadCardDto(1L, card.getOwner().getId(), card.getCardNumber(), card.getValidFrom(), card.getValidTo());
    try (var readCardDto = mockStatic(ReadCardDto.class)) {
      readCardDto.when(() -> ReadCardDto.fromEntity(any(Card.class)))
        .thenReturn(readCard);
      
      mockMvc.perform(post("/api/cards/add")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json(createCard)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(isSameCard());
    }
  }

}
