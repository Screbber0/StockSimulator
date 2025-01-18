package ru.screbber.stockSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class ParticipationDto {

    private Long id;

    private UserDto user;

    private TournamentDto tournament;

    private BigDecimal balance;

    private Map<String, Integer> stocks;
}
