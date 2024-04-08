package ru.zubkoff.sber.cardcrud.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.zubkoff.sber.cardcrud.api.transport.request.create.CreateCardRequest;
import ru.zubkoff.sber.cardcrud.api.transport.request.update.UpdateCardRequest;
import ru.zubkoff.sber.cardcrud.core.domain.card.Card;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardNumber;
import ru.zubkoff.sber.cardcrud.core.domain.card.CardServiceLife;
import ru.zubkoff.sber.cardcrud.core.domain.client.Client;
import ru.zubkoff.sber.cardcrud.core.domain.client.EmailAddress;
import ru.zubkoff.sber.cardcrud.core.domain.client.Name;
import ru.zubkoff.sber.cardcrud.core.domain.client.PassportNumber;
import ru.zubkoff.sber.cardcrud.core.services.CardService;

@WebMvcTest(CardApiController.class)
class CardApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CardService cardService;

  @Autowired
  private ObjectMapper objectMapper;

  Client client = new Client(1L,
      new PassportNumber("1234567890"),
      new Name("Иванов Иван Иванович"),
      new EmailAddress("ivanov@example.com"), List.of());

  Card card = new Card(1L,
      client,
      new CardNumber("1111222233334444"),
      new CardServiceLife(LocalDate.of(2000, 1, 1), LocalDate.of(3000, 1, 1)));

  String json(Object obj) throws JsonProcessingException {
    return objectMapper.writeValueAsString(obj);
  }

  ResultMatcher[] isSameCard() {
    return new ResultMatcher[] {
        jsonPath("$.id").value(card.getId()),
        jsonPath("$.cardNumber").value(card.getCardNumber().value()),
        jsonPath("$.ownerId").value(card.getOwner().getId()),
        jsonPath("$.validFrom").value(card.getServiceLife().validFrom().toString()),
        jsonPath("$.validTo").value(card.getServiceLife().validTo().toString())
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
        .andExpect(jsonPath("$[0].cardNumber").value(card.getCardNumber().value()))
        .andExpect(jsonPath("$[0].ownerId").value(card.getOwner().getId()))
        .andExpect(jsonPath("$[0].validFrom").value(card.getServiceLife().validFrom().toString()))
        .andExpect(jsonPath("$[0].validTo").value(card.getServiceLife().validTo().toString()));
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
    UpdateCardRequest updateCard = new UpdateCardRequest(
        card.getOwner().getId(),
        card.getCardNumber().value(),
        card.getServiceLife().validFrom(),
        card.getServiceLife().validTo());

    given(cardService.updateById(eq(targetId), any()))
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
    CreateCardRequest createCard = new CreateCardRequest(
        card.getOwner().getId(),
        card.getCardNumber().value(),
        card.getServiceLife().validFrom(),
        card.getServiceLife().validTo());

    given(cardService.createCard(any(), eq(card.getOwner().getId())))
        .willReturn(card);

    mockMvc.perform(post("/api/cards")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json(createCard)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(isSameCard());

  }

}
