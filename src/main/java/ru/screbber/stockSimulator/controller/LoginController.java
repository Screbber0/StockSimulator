package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.screbber.stockSimulator.entity.RegistrationDto;
import ru.screbber.stockSimulator.service.UserService;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        // Передадим пустую DTO, чтобы Thymeleaf мог привязывать поля
        model.addAttribute("registrationDto", new RegistrationDto());
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute RegistrationDto registrationDto,
            Model model) {
        try {
            if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
                model.addAttribute("error", "Passwords do not match.");
                return "register";
            }

            userService.registerUser(registrationDto.getUsername(), registrationDto.getPassword());
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
