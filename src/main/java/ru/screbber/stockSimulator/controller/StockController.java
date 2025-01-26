package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.screbber.stockSimulator.dto.StockInfoDto;
import ru.screbber.stockSimulator.service.StockSourceService;
import ru.screbber.stockSimulator.service.StockTradingService;
import ru.screbber.stockSimulator.service.TournamentService;

@Controller
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockSourceService stockSourceService;
    private final StockTradingService stockTradingService;
    private final TournamentService tournamentService;

    @GetMapping("/{ticker}")
    public String getStockDetail(@PathVariable String ticker, Model model) {
        // Получаем информацию об акции через сервис
        StockInfoDto stockInfo = stockSourceService.getStockInfo(ticker);

        // Кладём в модель для Thymeleaf
        model.addAttribute("stock", stockInfo);

        // Возвращаем имя шаблона
        return "stockDetail";
    }

    // TODO: сделать рефактиринг исключений и перенести в один сервис
    @PostMapping("/buy")
    public String buyStock(@RequestParam Long tournamentId, @RequestParam String ticker, @RequestParam int quantity) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Long participationId = tournamentService.getParticipationIdByUsernameAndTournamentId(username, tournamentId);

            stockTradingService.buyStock(participationId, ticker, quantity);
            return "redirect:/tournament/" + tournamentId;
    }


    @PostMapping("/sell")
    public String sellStock(@RequestParam Long tournamentId, @RequestParam String ticker, @RequestParam int quantity) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Long participationId = tournamentService.getParticipationIdByUsernameAndTournamentId(username, tournamentId);

            stockTradingService.sellStock(participationId, ticker, quantity);
            return "redirect:/tournament/" + tournamentId;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
