package ru.screbber.stockSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
public class RankingParticipantDto {

    private String username;

    private BigDecimal totalBalance;

    private Long rankPosition;

    public BigDecimal getTotalBalance() {
        return totalBalance.setScale(2, RoundingMode.HALF_UP);
    }
}
