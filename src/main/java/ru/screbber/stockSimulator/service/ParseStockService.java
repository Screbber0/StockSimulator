package ru.screbber.stockSimulator.service;

import ru.screbber.stockSimulator.dto.StockSuggestDto;

import java.util.List;

public interface ParseStockService {

    void processStockFile() throws Exception;

    /**
     * Найти акцию по префиксу тикера или названия (например, "SB" -> "SBER", "SBERP").
     */
    List<StockSuggestDto> findStocksByPrefix(String prefix);
}
