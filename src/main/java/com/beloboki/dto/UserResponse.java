package com.beloboki.dto;

import com.beloboki.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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

    public UserResponse(User user) {
        this.id = user.getId();
        this.active = user.getActive();
        this.surname = user.getSurname();
        this.name = user.getSurname();
        this.birthDate = user.getBirthDate();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.paymentCard =
                user.getPaymentCards().stream()
                        .map(PaymentCardResponse::new)
                        .collect(Collectors.toList());
    }
}
