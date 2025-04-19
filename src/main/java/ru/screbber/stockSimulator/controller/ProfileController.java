package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.screbber.stockSimulator.entity.UserEntity;
import ru.screbber.stockSimulator.service.UserService;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/user/profile")
    public String profilePage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }
}
