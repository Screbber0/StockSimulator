package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.screbber.stockSimulator.service.StockService;
import ru.screbber.stockSimulator.service.StockTradingService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final StockTradingService stockTradingService;

    @GetMapping("{ticker}")
    public BigDecimal GetStockPriceByTicker(@PathVariable String ticker) throws Exception {
        return stockService.getStockPriceByTicker(ticker);
    }

    // TODO: сделать рефактиринг исключений и перенести в один сервис
    @PostMapping("/buy")
    public String buyStock(@RequestParam Long participationId, @RequestParam String ticker, @RequestParam int quantity) {
        try {
            stockTradingService.buyStock(participationId, ticker, quantity);
            return "Stock purchased successfully!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/sell")
    public String sellStock(@RequestParam Long participationId, @RequestParam String ticker, @RequestParam int quantity) {
        try {
            stockTradingService.sellStock(participationId, ticker, quantity);
            return "Stock sold successfully!";
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
