package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.screbber.stockSimulator.dto.TournamentDto;
import ru.screbber.stockSimulator.service.TournamentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final TournamentService tournamentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TournamentDto> tournaments = tournamentService.getTournaments(username);

        model.addAttribute("tournaments", tournaments);
        return "dashboard";
    }
}

