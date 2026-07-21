package com.beloboki.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PaymentCardRequest {

    @NotBlank(message = "Card must have a number")
    @Pattern(
            regexp = "^\\d{13,19}$",
            message = "Payment card should have number from 13 to 19 numbers")
    @Size(min = 13, max = 19)
    private String number;

    @NotBlank(message = "Personal information of holder shouldn't be empty")
    private String holder;

    @NotBlank(message = "Expiration shouldn't be empty")
    private LocalDateTime expirationDate;

    @NotBlank(message = "Card status shouldn't be empty")
    private Boolean active;
}
