package ru.zubkoff.sber.cardcrud.core.domain.card;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ru.zubkoff.sber.cardcrud.core.domain.client.Client;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "unique_card_number", columnNames = { "card_number" })
})
public class Card implements Serializable {

  private static final Period CARD_VALIDITY_PERIOD = Period.ofYears(5);

  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "client_id", nullable = false)
  private Client owner;

  @Embedded
  private CardNumber cardNumber;

  @Embedded
  private CardServiceLife serviceLife;

  public Card() {

  }

  public Card(Long id, Client owner, CardNumber cardNumber, CardServiceLife serviceLife) {
    this.id = id;
    this.owner = owner;
    this.cardNumber = cardNumber;
    this.serviceLife = serviceLife;
  }

  public Card reissued(CardNumber newCardNumber, LocalDate now) {
    return new Card(null, owner, newCardNumber, new CardServiceLife(now, now.plus(CARD_VALIDITY_PERIOD)));
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Client getOwner() {
    return owner;
  }

  public void setOwner(Client owner) {
    this.owner = owner;
  }

  public CardServiceLife getServiceLife() {
    return serviceLife;
  }

  public void setServiceLife(CardServiceLife serviceLife) {
    this.serviceLife = serviceLife;
  }

  public static Period getCardValidityPeriod() {
    return CARD_VALIDITY_PERIOD;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public CardNumber getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(CardNumber cardNumber) {
    this.cardNumber = cardNumber;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, owner, cardNumber, serviceLife);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj instanceof Card other) {
      return id == other.id && cardNumber.equals(other.cardNumber)
          && serviceLife.equals(other.serviceLife);
    }
    return false;

  }

}
