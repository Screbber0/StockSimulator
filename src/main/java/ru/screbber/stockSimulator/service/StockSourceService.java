package ru.screbber.stockSimulator.service;

import ru.screbber.stockSimulator.dto.StockInfoDto;

import java.math.BigDecimal;


public interface StockSourceService {

    /**
     * Вернуть цену акции по тикеру (например, "SBER").
     */
    BigDecimal getStockPriceByTicker(String ticker);

    /**
     * Вернуть информацию об акции по тикеру (например, "SBER").
     */
    StockInfoDto getStockInfo(String ticker);
}
