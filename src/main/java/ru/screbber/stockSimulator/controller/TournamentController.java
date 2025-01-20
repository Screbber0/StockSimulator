package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.screbber.stockSimulator.service.StockTradingService;
import ru.screbber.stockSimulator.service.TournamentService;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("tournament")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;
    private final StockTradingService stockTradingService;

    @PostMapping("/create")
    public String createTournament(@RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            tournamentService.createTournament(name);
            // redirectAttributes.addFlashAttribute("successMessage", "Турнир успешно создан!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            // redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/join")
    public String joinTournament(@RequestParam Long tournamentId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            tournamentService.joinTournament(username, tournamentId);
            return "redirect:/dashboard";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/{tournamentId}")
    public String tournament(@PathVariable Long tournamentId, Model model) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BigDecimal balance = tournamentService.getUserBalanceInTournament(username, tournamentId);
        Long participationId = tournamentService.getParticipationByUsernameAndTournamentId(username, tournamentId);
        Map<String, Integer> userStocks = stockTradingService.getUserStocks(participationId);

        model.addAttribute("tournamentId", tournamentId);
        model.addAttribute("balance", balance);
        model.addAttribute("stocks", userStocks);

        return "tournament";
    }
}
