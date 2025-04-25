package ru.screbber.stockSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
public class RankingTeamDto {

    private Long rank;

    private String teamName;

    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance.setScale(2, RoundingMode.HALF_UP);
    }
}

