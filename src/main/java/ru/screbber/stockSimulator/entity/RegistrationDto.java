package ru.screbber.stockSimulator.entity;


import lombok.Data;

@Data
public class RegistrationDto {

    // @NotBlank
    private String username;

    // @NotBlank
    private String password;

    // @NotBlank
    private String confirmPassword;
}
