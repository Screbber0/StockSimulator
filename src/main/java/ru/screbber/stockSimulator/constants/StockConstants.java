package ru.screbber.stockSimulator.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StockConstants {

    MOEX("TQBR");

    private final String value;
}
