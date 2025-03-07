package ru.screbber.stockSimulator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class ParticipantStockPositionDto {

    private String ticker;            // "AAPL"

    private String description;       // "Apple Inc"

    private BigDecimal currentPrice;  // 220.60

    private BigDecimal todayChange;   // +0.96

    private BigDecimal todayChangePct;// +0.22

    private BigDecimal purchasePrice; // 220.12

    private int quantity;            // 2

    // Подсчитанные поля:
    private BigDecimal totalValue;    // currentPrice * quantity
    private BigDecimal totalGainLoss; // (currentPrice - purchasePrice) * quantity
    private BigDecimal totalGainLossPercent; // процент относительно purchasePrice

    public BigDecimal getCurrentPrice() {
        return currentPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTodayChange() {
        return todayChange.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTodayChangePct() {
        return todayChangePct.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalValue() {
        return totalValue.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalGainLoss() {
        return totalGainLoss.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalGainLossPercent() {
        return totalGainLossPercent.setScale(2, RoundingMode.HALF_UP);
    }

    // Готовые вычисляемые поля (или можно считать на лету в шаблоне)
/*    public BigDecimal getTotalValue() {
        if (currentPrice == null) return BigDecimal.ZERO;
        return currentPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalGainLoss() {
        if (currentPrice == null || purchasePrice == null) return BigDecimal.ZERO;
        BigDecimal diff = currentPrice.subtract(purchasePrice);
        return diff.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalGainLossPercent() {
        // Например, считаем относительный % с точки зрения purchasePrice
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getTotalGainLoss()
                .divide(purchasePrice.multiply(BigDecimal.valueOf(quantity)), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }*/
}
