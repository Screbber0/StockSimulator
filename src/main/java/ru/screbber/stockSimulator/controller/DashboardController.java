package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.screbber.stockSimulator.dto.TournamentDto;
import ru.screbber.stockSimulator.service.ParticipationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ParticipationService participationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // Получаем имя текущего пользователя
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Загружаем турниры, в которых участвует пользователь
        List<TournamentDto> tournaments = participationService.getUserTournaments(username);

        // Передаем список турниров в модель
        model.addAttribute("tournaments", tournaments);

        return "dashboard"; // Возвращаем шаблон "dashboard.html"
    }
}

