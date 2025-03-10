package ru.screbber.stockSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockSuggestDto {

    private String ticker;

    private String emitent;
}
