package ru.screbber.stockSimulator.service;

import java.math.BigDecimal;


public interface StockSourceService {

    BigDecimal getStockPriceByTicker(String ticker) throws Exception;
}
