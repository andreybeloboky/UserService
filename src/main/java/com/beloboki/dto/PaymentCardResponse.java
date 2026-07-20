package com.beloboki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PaymentCardResponse {

    @JsonProperty private Long id;

    @JsonProperty private String number;

    @JsonProperty private String holder;

    @JsonProperty private LocalDateTime expirationDate;

    @JsonProperty private Boolean active;

    @JsonProperty private LocalDateTime createdAt;

    @JsonProperty private LocalDateTime updatedAt;
}
