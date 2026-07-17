package com.beloboki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentCardRequest {

    @JsonProperty
    @NotBlank(message = "Card must have a number")
    @Pattern(
            regexp = "^\\d{13,19}$",
            message = "Payment card should have number from 13 to 19 numbers"
    )
    @Size(min = 13, max = 19)
    private String number;

    @JsonProperty
    @NotBlank(message = "Personal information of holder shouldn't be empty")
    private String holder;

    @JsonProperty
    @NotBlank(message = "Expiration shouldn't be empty")
    private LocalDateTime expirationDate;

    @JsonProperty
    @NotBlank(message = "Card status shouldn't be empty")
    private Boolean active;

    @JsonProperty
    @NotBlank(message = "User_id shouldn't be empty")
    private Long userId;
}
