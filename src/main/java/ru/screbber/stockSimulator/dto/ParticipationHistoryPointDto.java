package ru.screbber.stockSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
public class ParticipationHistoryPointDto {

    private String date;

    private BigDecimal price;

    public BigDecimal getPrice() {
        return price.setScale(1, RoundingMode.HALF_UP);
    }
}
