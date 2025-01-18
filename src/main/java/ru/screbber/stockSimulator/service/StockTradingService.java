package ru.screbber.stockSimulator.service;

import java.util.Map;

public interface StockTradingService {

    void buyStock(Long participationId, String ticker, int quantity) throws Exception;

    void sellStock(Long participationId, String ticker, int quantity) throws Exception;

    Map<String, Integer> getUserStocks(Long participationId) throws Exception;

}
