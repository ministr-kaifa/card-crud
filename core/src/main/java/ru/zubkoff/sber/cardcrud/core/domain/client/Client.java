package ru.zubkoff.sber.cardcrud.core.domain.client;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ru.zubkoff.sber.cardcrud.core.domain.card.Card;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "unique_passport_number", columnNames = { "passport_number" }),
    @UniqueConstraint(name = "unique_email", columnNames = { "email" }),
})
@NamedEntityGraph(name = "client-with-cards-graph", attributeNodes = {
    @NamedAttributeNode("cards")
})
public class Client implements Serializable {
  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  private PassportNumber passportNumber;

  @Embedded
  private Name name;

  @Embedded
  private EmailAddress email;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  private List<Card> cards;

  public Client() {

  }

  public Client(Long id, PassportNumber passportNumber, Name name, EmailAddress email, List<Card> cards) {
    this.id = id;
    this.passportNumber = passportNumber;
    this.name = name;
    this.cards = cards;
    this.email = email;
  }

  public Long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<Card> getCards() {
    return cards;
  }

  public void setCards(List<Card> cards) {
    this.cards = cards;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PassportNumber getPassportNumber() {
    return passportNumber;
  }

  public void setPassportNumber(PassportNumber passportNumber) {
    this.passportNumber = passportNumber;
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public EmailAddress getEmail() {
    return email;
  }

  public void setEmail(EmailAddress email) {
    this.email = email;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, passportNumber, name, email);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj instanceof Client other) {
      return id == other.id && other.passportNumber.equals(passportNumber) && other.name.equals(name)
          && other.email.equals(email);
    }
    return false;
  }

}
