package ru.screbber.stockSimulator.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockInfoDto {
    // Символьный тикер акции, например "SBER", "GAZP", "YNDX"
    private String ticker;

    // Название компании
    private String companyName;

    // Текущая цена
    private BigDecimal currentPrice;

    // Изменение цены за день (например, +2.5 руб.)
    private BigDecimal change;

    // Процентное изменение за день (например, +1.1%)
    private BigDecimal changePercent;

    // Максимум за день
    private BigDecimal dayHigh;

    // Минимум за день
    private BigDecimal dayLow;

    // Объём (число акций)
    private BigDecimal volume;
}
