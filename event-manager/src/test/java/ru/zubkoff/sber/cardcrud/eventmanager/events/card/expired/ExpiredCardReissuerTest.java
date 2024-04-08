package ru.zubkoff.sber.cardcrud.eventmanager.events.card.expired;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ru.zubkoff.sber.cardcrud.core.domain.card.Card;
import ru.zubkoff.sber.cardcrud.core.domain.client.Client;
import ru.zubkoff.sber.cardcrud.core.services.CardService;

@SpringBootTest(classes = {
    CardService.class,
    CardExpiredEventGeneratorPublisher.class,
    CardExpiredReissuer.class })
class ExpiredCardReissuerTest {

  @MockBean
  CardService service;

  @Autowired
  CardExpiredEventGeneratorPublisher publisher;

  @Autowired
  CardExpiredReissuer reissuer;

  @Test
  void givenExpiredCards_whenHandleExpiredCardsAsEvent_thenAllCardsWasReissued() {
    // given
    var owner = new Client(1L, null, null, null, null);
    var expiredCards = List.of(
        new Card(1L, owner, null, null),
        new Card(2L, owner, null, null),
        new Card(3L, owner, null, null));
    given(service.findCardsByValidToLessThanEqual(any())).willReturn(expiredCards);

    // when
    publisher.generateAndPublish();

    // then
    ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
    verify(service, times(expiredCards.size())).reissueCard(captor.capture());
    List<Card> reissuedCards = captor.getAllValues();
    assertArrayEquals(reissuedCards.toArray(), expiredCards.toArray());
  }

}
