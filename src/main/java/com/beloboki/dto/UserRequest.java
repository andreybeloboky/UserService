package com.beloboki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Data;

@Data
public class UserRequest {

    @JsonProperty
    @NotBlank(message = "Name shouldn't be empty")
    private String name;

    @JsonProperty
    @NotBlank(message = "Surname shouldn't be empty")
    private String surname;

    @JsonProperty
    @NotBlank(message = "Birth date shouldn't be empty")
    private LocalDate birthDate;

    @JsonProperty
    @NotBlank(message = "Email shouldn't be empty")
    @Email
    private String email;

    @JsonProperty
    @NotBlank(message = "Status shouldn't be empty")
    private Boolean active;
}
