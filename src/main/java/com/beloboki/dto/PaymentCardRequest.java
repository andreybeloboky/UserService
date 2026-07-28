package com.beloboki.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record PaymentCardRequest(
        @NotBlank(message = "Personal information of holder shouldn't be empty") String holder,
        @NotBlank(message = "Card must have a number")
                @Pattern(
                        regexp = "^\\d{16}$",
                        message = "Payment card should have number from 16 numbers")
                String number,
        @NotNull(message = "Expiration shouldn't be empty") @Future LocalDateTime expirationDate,
        @NotNull(message = "Card status shouldn't be empty") Boolean active) {}
