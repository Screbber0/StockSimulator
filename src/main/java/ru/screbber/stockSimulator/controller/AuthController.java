package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.screbber.stockSimulator.entity.UserEntity;
import ru.screbber.stockSimulator.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Hi from welcome endpoint";
    }

    @PostMapping("/register")
    public String registration(@RequestBody UserEntity user) {
        userService.createUser(user);
        return "User was reg";
    }
}
