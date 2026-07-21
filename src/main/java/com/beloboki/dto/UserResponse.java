package com.beloboki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "id",
        "name",
        "surname",
        "birthDate",
        "email",
        "active",
        "createdAt",
        "updatedAt",
        "paymentCards"
})
public class UserResponse {

    @JsonProperty private Long id;

    @JsonProperty private String name;

    @JsonProperty private String surname;

    @JsonProperty private LocalDate birthDate;

    @JsonProperty private String email;

    @JsonProperty private Boolean active;

    @JsonProperty private LocalDateTime createdAt;

    @JsonProperty private LocalDateTime updatedAt;

    @JsonProperty private List<PaymentCardResponse> paymentCards;
}
