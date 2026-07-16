package com.beloboki.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_cards")
@Setter
@Getter
public class PaymentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "number", nullable = false)
    private long number;

    @Column(name = "holder", nullable = false)
    private String holder;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expiration_date;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PaymentCard() {
    }
}
