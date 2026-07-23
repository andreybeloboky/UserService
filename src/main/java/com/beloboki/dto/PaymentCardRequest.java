package com.beloboki.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PaymentCardRequest {

    @NotBlank(message = "Card must have a number")
    @Pattern(
            regexp = "^\\d{13,15}$",
            message = "Payment card should have number from 13 to 15 numbers")
    @Size(min = 13, max = 15)
    private String number;

    @NotBlank(message = "Personal information of holder shouldn't be empty")
    private String holder;

    @NotNull(message = "Expiration shouldn't be empty")
    @Future
    private LocalDateTime expirationDate;

    @NotNull(message = "Card status shouldn't be empty")
    private Boolean active;
}
