package com.beloboki.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PaymentCardResponse {

    private Long id;

    private String number;

    private String holder;

    private LocalDateTime expirationDate;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
