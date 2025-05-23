package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.screbber.stockSimulator.dto.StockSuggestDto;
import ru.screbber.stockSimulator.service.ParseStockService;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockSearchController {

    private final ParseStockService parseStockService;

    @GetMapping("/search")
    public List<StockSuggestDto> searchTickers(@RequestParam String term) {
        return parseStockService.findStocksByPrefix(term);
    }


    @GetMapping("/process-stocks")
    public String processStockFile() {
        try {
            parseStockService.processStockFile();
            return "File processed successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
