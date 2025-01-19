package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.screbber.stockSimulator.entity.UserEntity;
import ru.screbber.stockSimulator.service.UserService;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/register")
    public String registration(@RequestBody UserEntity user) {
        userService.createUser(user);
        return "User was reg";
    }
}
