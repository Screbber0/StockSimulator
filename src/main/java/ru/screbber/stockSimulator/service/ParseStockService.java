package ru.screbber.stockSimulator.service;

import java.util.List;

public interface ParseStockService {

    void processStockFile() throws Exception;

    /**
     * Найти акцию по префиксу тикера (например, "SB" -> "SBER", "SBERP").
     */
    List<String> findTickersByPrefix(String prefix);
}
