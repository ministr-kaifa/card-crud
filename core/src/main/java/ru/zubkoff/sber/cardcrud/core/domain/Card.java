package ru.zubkoff.sber.cardcrud.core.domain;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { 
  @UniqueConstraint(name = "unique_card_number", columnNames = { "cardNumber" })
})
public class Card implements Serializable {
  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "client_id", nullable = false)
  private Client owner;

  @Column(nullable = false)
  private String cardNumber;

  @Column(nullable = false)
  private LocalDate validFrom;

  @Column(nullable = false)
  private LocalDate validTo;

  public Card() {
    super();
  }
  
  public Card(Long id, Client owner, String cardNumber, LocalDate validFrom, LocalDate validTo) {
    this.id = id;
    this.owner = owner;
    this.cardNumber = cardNumber;
    this.validFrom = validFrom;
    this.validTo = validTo;
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
  
  public String getCardNumber() {
    return cardNumber;
  }
  
  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }
  
  public LocalDate getValidFrom() {
    return validFrom;
  }
  
  public void setValidFrom(LocalDate validFrom) {
    this.validFrom = validFrom;
  }
  
  public LocalDate getValidTo() {
    return validTo;
  }
  
  public void setValidTo(LocalDate validTo) {
    this.validTo = validTo;
  }

}

