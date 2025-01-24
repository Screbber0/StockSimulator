package ru.screbber.stockSimulator.service;

import ru.screbber.stockSimulator.dto.StockPositionDto;

import java.util.List;

public interface StockTradingService {

    void buyStock(Long participationId, String ticker, int quantity);

    void sellStock(Long participationId, String ticker, int quantity);

    List<StockPositionDto> getUserStockPositions(Long participationId);
}
