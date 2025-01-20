package ru.screbber.stockSimulator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateTournamentDto {

    private String name;
    private String dateRange;
    private BigDecimal initialCapital;
    private int maxParticipants;

    private LocalDate startDate;
    private LocalDate endDate;

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
        if (dateRange != null) {
            String[] parts = dateRange.split(" ➡ ");
            this.startDate = LocalDate.parse(parts[0]);
            this.endDate = LocalDate.parse(parts[1]);
        }
    }
}
