package com.beloboki.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_user_name", columnList = "name"),
                @Index(name = "idx_user_surname", columnList = "surname")
        })
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birth_date;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "active", nullable = false)
    boolean active;

    @Column(name = "createdAt")
    @CreatedDate
    LocalDateTime createdAt;

    @Column(name = "updatedAt")
    @LastModifiedDate
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private List<PaymentCard> paymentCardList = new ArrayList<>();

    public User() {
    }
}
