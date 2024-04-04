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
import ru.zubkoff.sber.cardcrud.api.transport.CreateCardRequest;
import ru.zubkoff.sber.cardcrud.api.transport.ReadCardResponse;
import ru.zubkoff.sber.cardcrud.api.transport.UpdateCardRequest;
import ru.zubkoff.sber.cardcrud.core.services.CardService;

@RestController
@CrossOrigin(origins = { "*" })
public class CardApiController {

  private static final int PAGE_SIZE = 40;

  private final CardService cardService;

  public CardApiController(CardService cardService) {
    this.cardService = cardService;
  }

  @GetMapping("/api/cards")
  public List<ReadCardResponse> getAllCards(@RequestParam(defaultValue = "0") int page) {
    return cardService.findAllCards(page, PAGE_SIZE).stream()
        .map(ReadCardResponse::fromEntity)
        .toList();
  }

  @GetMapping("/api/cards/{cardId}")
  public ReadCardResponse getCard(@PathVariable Long cardId) {
    return ReadCardResponse.fromEntity(cardService.findCardById(cardId));
  }

  @PostMapping("/api/cards")
  @ResponseStatus(code = HttpStatus.CREATED)
  public ReadCardResponse createCard(@RequestBody @Valid CreateCardRequest createCardDto) {
    var card = createCardDto.toEntity();
    cardService.createCard(card, createCardDto.ownerId());
    return ReadCardResponse.fromEntity(card);
  }

  @DeleteMapping("/api/cards/{cardId}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteCard(@PathVariable Long cardId) {
    cardService.deleteCard(cardId);
  }

  @PatchMapping("/api/cards/{cardId}")
  public ReadCardResponse updateCard(@PathVariable Long cardId, @RequestBody @Valid UpdateCardRequest updateCardDto) {
    var card = updateCardDto.toEntity();
    return ReadCardResponse.fromEntity(cardService.mergeById(cardId, card));
  }

}
