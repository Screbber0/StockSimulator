package ru.screbber.stockSimulator.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.screbber.stockSimulator.dto.RegistrationDto;
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
        model.addAttribute("registrationDto", new RegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute RegistrationDto registrationDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "register";
        }
        try {
            if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
                model.addAttribute("error", "Пароли не совпадают.");
                return "register";
            }
            userService.registerUser(
                    registrationDto.getUsername(),
                    registrationDto.getEmail(),
                    registrationDto.getPassword());
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }


    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("token") String token, Model model) {
        boolean success = userService.confirmEmail(token);
        if (success) {
            model.addAttribute("message", "Ваш email успешно подтвержден. Теперь вы можете войти.");
            return "login";
        } else {
            model.addAttribute("error", "Неверный или просроченный токен.");
            return "error";
        }
    }
}
