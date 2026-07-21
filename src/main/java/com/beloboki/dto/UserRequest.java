package com.beloboki.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Name shouldn't be empty")
    private String name;

    @NotBlank(message = "Surname shouldn't be empty")
    private String surname;

    @NotBlank(message = "Birth date shouldn't be empty")
    @Past
    private LocalDate birthDate;

    @NotBlank(message = "Email shouldn't be empty")
    @Email
    private String email;

    @NotBlank(message = "Status shouldn't be empty")
    private Boolean active;
}
