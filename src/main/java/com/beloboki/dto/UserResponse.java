package com.beloboki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class UserResponse {

    @JsonProperty private Long id;

    @JsonProperty private String name;

    @JsonProperty private String surname;

    @JsonProperty private LocalDate birthDate;

    @JsonProperty private String email;

    @JsonProperty private Boolean active;

    @JsonProperty private LocalDateTime createdAt;

    @JsonProperty private LocalDateTime updatedAt;

    @JsonProperty private List<PaymentCardResponse> paymentCard;
}
