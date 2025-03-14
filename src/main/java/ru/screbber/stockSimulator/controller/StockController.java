package ru.screbber.stockSimulator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.screbber.stockSimulator.dto.StockCandlePointDto;
import ru.screbber.stockSimulator.dto.StockInfoDto;
import ru.screbber.stockSimulator.service.StockSourceService;
import ru.screbber.stockSimulator.service.StockTradingService;
import ru.screbber.stockSimulator.service.TournamentService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockSourceService stockSourceService;
    private final StockTradingService stockTradingService;
    private final TournamentService tournamentService;
    private final ObjectMapper mapper;

    @GetMapping("/{ticker}")
    public String getStockDetail(@PathVariable String ticker,
                                 @RequestParam(required = false) String from,
                                 @RequestParam(required = false) String to,
                                 Model model) throws JsonProcessingException {

        // 1. Подставляем дефолтные даты, если не переданы
        Instant fromDate = (from == null)
                ? Instant.now().minus(3, ChronoUnit.DAYS) // за 3 дней
                : Instant.parse(from);
        Instant toDate = (to == null)
                ? Instant.now()
                : Instant.parse(to);

        // 2. Получаем информацию об акции (текущую цену, инфо)
        StockInfoDto stockInfo = stockSourceService.getStockInfo(ticker);

        // 3. Получаем исторические свечи
        List<StockCandlePointDto> history = stockSourceService.getHistoricPrices(ticker, fromDate, toDate);

        String candlesJson = mapper.writeValueAsString(history);

        // 4. Преобразуем в JSON, используя уже сконфигурированный mapper
        model.addAttribute("stock", stockInfo);
        model.addAttribute("candlesJson", candlesJson);
        return "stockDetail";
    }

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
