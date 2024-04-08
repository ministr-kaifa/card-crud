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
import ru.zubkoff.sber.cardcrud.api.transport.request.create.CreateCardRequest;
import ru.zubkoff.sber.cardcrud.api.transport.request.update.UpdateCardRequest;
import ru.zubkoff.sber.cardcrud.api.transport.response.read.ReadCardResponse;
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
  public ReadCardResponse getCard(@PathVariable long cardId) {
    return ReadCardResponse.fromEntity(cardService.findCardById(cardId));
  }

  @PostMapping("/api/cards")
  @ResponseStatus(code = HttpStatus.CREATED)
  public ReadCardResponse createCard(@RequestBody @Valid CreateCardRequest request) {
    var cardCreation = request.toCardCreation();
    return ReadCardResponse.fromEntity(cardService.createCard(cardCreation, request.ownerId()));
  }

  @DeleteMapping("/api/cards/{cardId}")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void deleteCard(@PathVariable long cardId) {
    cardService.deleteCard(cardId);
  }

  @PatchMapping("/api/cards/{cardId}")
  public ReadCardResponse updateCard(@PathVariable long cardId, @RequestBody @Valid UpdateCardRequest request) {
    var card = request.toCardUpdate();
    return ReadCardResponse.fromEntity(cardService.updateById(cardId, card));
  }

}
