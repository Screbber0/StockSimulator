package ru.screbber.stockSimulator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ParticipationDto {

    private Long id;

    private UserDto user;

    private TournamentDto tournament;

    private BigDecimal cash;
}
