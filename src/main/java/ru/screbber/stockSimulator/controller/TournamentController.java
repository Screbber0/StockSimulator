package ru.screbber.stockSimulator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.screbber.stockSimulator.dto.CreateTournamentDto;
import ru.screbber.stockSimulator.dto.ParticipantStockPositionDto;
import ru.screbber.stockSimulator.dto.ParticipationHistoryPointDto;
import ru.screbber.stockSimulator.dto.RankingParticipantDto;
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
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            tournamentService.createTournamentAndJoin(createTournamentDto, username);
            return "redirect:/dashboard";
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/join")
    public String joinTournament(@RequestParam String tournamentName) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            tournamentService.joinTournament(username, tournamentName);
            return "redirect:/dashboard";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/{tournamentId}")
    public String tournament(@PathVariable Long tournamentId, Model model) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long participationId = tournamentService.getParticipationIdByUsernameAndTournamentId(username, tournamentId);

        BigDecimal cash = tournamentService.getUserCashByParticipationId(participationId);
        List<ParticipantStockPositionDto> userStockPositions = stockTradingService.getUserStockPositions(participationId);
        BigDecimal totalBalance = tournamentService.getUserTotalBalanceByParticipationIdAndUserStockPositionList(participationId, userStockPositions);
        Long rank = tournamentService.getRankingByParticipation(participationId);

        model.addAttribute("tournamentId", tournamentId);
        model.addAttribute("cash", cash);
        model.addAttribute("stocks", userStockPositions);
        model.addAttribute("totalBalance", totalBalance);
        model.addAttribute("rank", rank);

        List<ParticipationHistoryPointDto> historyData = tournamentService.getParticipationHistory(participationId);
        ObjectMapper mapper = new ObjectMapper();
        String historyDataJson = mapper.writeValueAsString(historyData);
        model.addAttribute("historyDataJson", historyDataJson);

        return "tournament";
    }

    @GetMapping("/{tournamentId}/ranking")
    public String tournamentRanking(@PathVariable Long tournamentId, Model model) {
        List<RankingParticipantDto> rankingList = tournamentService.getTournamentRankingList(tournamentId);

        model.addAttribute("rankingList", rankingList);
        model.addAttribute("tournamentId", tournamentId);

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("currentUsername", currentUsername);

        return "tournamentRanking";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<String> searchTournamentByPrefix(@RequestParam String term) {
        return tournamentService.findTickersByPrefix(term);
    }
}
