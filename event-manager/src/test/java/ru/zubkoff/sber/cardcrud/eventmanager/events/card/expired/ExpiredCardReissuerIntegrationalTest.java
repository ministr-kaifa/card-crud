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

import ru.zubkoff.sber.cardcrud.core.domain.Card;
import ru.zubkoff.sber.cardcrud.core.domain.Client;
import ru.zubkoff.sber.cardcrud.core.services.CardService;

@SpringBootTest(classes = {
    CardService.class,
    CardExpiredEventGeneratorPublisher.class,
    CardExpiredReissuer.class })
class ExpiredCardReissuerIntegrationalTest {

  @MockBean
  CardService service;

  @Autowired
  CardExpiredEventGeneratorPublisher publisher;

  @Autowired
  CardExpiredReissuer reissuer;

  @Test
  void givenExpiredCards_whenHandleExpiredCardsAsEvent_thenAllCardsWasReissued() {
    // given
    var expiredCards = List.of(new Card(1L, null, null, null, null), new Card(2L, null, null, null, null),
        new Card(3L, null, null, null, null));
    expiredCards.forEach(card -> card.setOwner(new Client(1L, null, null, null, null)));
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
