package ru.zubkoff.sber.cardcrud.core.domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = { 
  @UniqueConstraint(name = "unique_passport_number", columnNames = { "passportNumber" }),
  @UniqueConstraint(name = "unique_email", columnNames = { "email" }),
})
public class Client implements Serializable {
  @Id
  @Column(nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String passportNumber;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  private List<Card> cards;

  public Client() {
    
  }
  
  public Client(Long id, String passportNumber, String name, String email, List<Card> cards) {
    this.id = id;
    this.passportNumber = passportNumber;
    this.name = name;
    this.cards = cards;
    this.email = email;
  }

  public long getId() {
    return id;
  }
  
  public void setId(long id) {
    this.id = id;
  }
  
  public String getPassportNumber() {
    return passportNumber;
  }
  
  public void setPassportNumber(String passportNumber) {
    this.passportNumber = passportNumber;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public List<Card> getCards() {
    return cards;
  }

  public void setCards(List<Card> cards) {
    this.cards = cards;
  }
  
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

}
