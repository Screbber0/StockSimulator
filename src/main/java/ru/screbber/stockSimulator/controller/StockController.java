package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.screbber.stockSimulator.service.StockSourceService;
import ru.screbber.stockSimulator.service.StockTradingService;
import ru.screbber.stockSimulator.service.TournamentService;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockSourceService stockSourceService;
    private final StockTradingService stockTradingService;
    private final TournamentService tournamentService;

    @GetMapping("{ticker}")
    public BigDecimal GetStockPriceByTicker(@PathVariable String ticker) throws Exception {
        return stockSourceService.getStockPriceByTicker(ticker);
    }

    // TODO: сделать рефактиринг исключений и перенести в один сервис
    @PostMapping("/buy")
    public String buyStock(@RequestParam Long tournamentId, @RequestParam String ticker, @RequestParam int quantity) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Long participationId = tournamentService.getParticipationByUsernameAndTournamentId(username, tournamentId);

            stockTradingService.buyStock(participationId, ticker, quantity);
            return "redirect:/tournament/" + tournamentId;
        } catch (Exception e) {
            return "error";
        }
    }


    @PostMapping("/sell")
    public String sellStock(@RequestParam Long tournamentId, @RequestParam String ticker, @RequestParam int quantity) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Long participationId = tournamentService.getParticipationByUsernameAndTournamentId(username, tournamentId);

            stockTradingService.sellStock(participationId, ticker, quantity);
            return "redirect:/tournament/" + tournamentId;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/view")
    public Map<String, Integer> viewStocks(@RequestParam Long participationId) {
        try {
            return stockTradingService.getUserStocks(participationId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
