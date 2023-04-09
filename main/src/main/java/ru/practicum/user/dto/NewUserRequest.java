package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NewUserRequest {
    @NotBlank(message = "Field: email. Error: must not be blank.")
    @Email(message = "Field: email. Error: incorrect email.")
    private String email;
    @NotBlank(message = "Field: name. Error: must not be blank.")
    private String name;
}
