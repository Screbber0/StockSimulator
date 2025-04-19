package ru.screbber.stockSimulator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDto {

    @NotBlank(message = "Имя пользователя не должно быть пустым.")
    @Size(min = 3, max = 20, message = "Имя пользователя должно содержать от 3 до 20 символов.")
    private String username;

    @NotBlank(message = "Электронная почта не должна быть пустой.")
    @Email(message = "Некорректный формат электронной почты.")
    private String email;

    @NotBlank(message = "Пароль не должен быть пустым.")
    private String password;

    @NotBlank(message = "Подтверждение пароля не должно быть пустым.")
    private String confirmPassword;
}
