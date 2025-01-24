package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.screbber.stockSimulator.dto.CreateTournamentDto;
import ru.screbber.stockSimulator.dto.StockPositionDto;
import ru.screbber.stockSimulator.service.StockTradingService;
import ru.screbber.stockSimulator.service.TournamentService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("tournament")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;
    private final StockTradingService stockTradingService;

    @GetMapping("/create")
    public String showCreateTournamentPage(Model model) {
        model.addAttribute("createTournamentDto", new CreateTournamentDto());
        return "createTournament";
    }

    @PostMapping("/create")
    public String createTournament(@ModelAttribute CreateTournamentDto createTournamentDto) {
        try {
            tournamentService.createTournament(createTournamentDto);
            return "redirect:/dashboard";
        } catch (Exception e) {
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

        BigDecimal cash = tournamentService.getUserCashInTournament(username, tournamentId);
        Long participationId = tournamentService.getParticipationByUsernameAndTournamentId(username, tournamentId);
        List<StockPositionDto> userStockPositions = stockTradingService.getUserStockPositions(participationId);

        model.addAttribute("tournamentId", tournamentId);
        model.addAttribute("cash", cash);
        model.addAttribute("stocks", userStockPositions);

        return "tournament";
    }
}
