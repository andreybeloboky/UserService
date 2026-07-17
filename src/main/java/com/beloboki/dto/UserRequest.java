package com.beloboki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

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
    private String email;

    @JsonProperty
    @NotBlank(message = "Status shouldn't be empty")
    private Boolean active;
}
