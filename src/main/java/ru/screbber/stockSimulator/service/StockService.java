package ru.screbber.stockSimulator.service;

import java.math.BigDecimal;


public interface StockService {

    BigDecimal getStockPriceByTicker(String ticker) throws Exception;
}
