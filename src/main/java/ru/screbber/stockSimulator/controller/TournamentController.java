package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.screbber.stockSimulator.entity.TournamentEntity;
import ru.screbber.stockSimulator.service.ParticipationService;
import ru.screbber.stockSimulator.service.TournamentService;

import java.math.BigDecimal;

@Controller
// @RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    private final ParticipationService participationService;

    @PostMapping("/create")
    public TournamentEntity createTournament(@RequestParam String name) {
        try {
            return tournamentService.createTournament(name);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/tournament")
    public String tournament(@RequestParam Long tournamentId, Model model) {
        // Получаем имя текущего пользователя
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Получаем баланс пользователя в турнире
        BigDecimal balance = participationService.getUserBalanceInTournament(username, tournamentId);

        // Добавляем данные в модель
        model.addAttribute("tournamentId", tournamentId);
        model.addAttribute("balance", balance);

        return "tournament";
    }

    @PostMapping("/tournament/join")
    public String joinTournament(@RequestParam Long tournamentId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            participationService.joinTournament(username, tournamentId);
            return "redirect:/dashboard";
        } catch (Exception e) {
            return "error";
        }
    }
}
