package com.beloboki.dto;

import com.beloboki.model.PaymentCard;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentCardResponse {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String number;

    @JsonProperty
    private String holder;

    @JsonProperty
    private LocalDateTime expirationDate;

    @JsonProperty
    private Boolean active;

    @JsonProperty
    private LocalDateTime createdAt;

    @JsonProperty
    private LocalDateTime updatedAt;

    public PaymentCardResponse(PaymentCard paymentCard) {
        this.id = paymentCard.getId();
        this.number = paymentCard.getNumber();
        this.holder = paymentCard.getHolder();
        this.expirationDate = paymentCard.getExpirationDate();
        this.active = paymentCard.getActive();
        this.createdAt = paymentCard.getCreatedAt();
        this.updatedAt = paymentCard.getUpdatedAt();
    }
}
