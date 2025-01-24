package ru.screbber.stockSimulator.service;

import ru.screbber.stockSimulator.dto.CreateTournamentDto;
import ru.screbber.stockSimulator.dto.ParticipationDto;
import ru.screbber.stockSimulator.dto.TournamentDto;

import java.math.BigDecimal;
import java.util.List;

public interface TournamentService {

    void createTournament(CreateTournamentDto dto);

    ParticipationDto joinTournament(String username, Long tournamentId) throws Exception;

    List<TournamentDto> getTournaments(String username);

    BigDecimal getUserBalanceInTournament(String username, Long tournamentId);

    Long getParticipationByUsernameAndTournamentId(String username, Long tournamentId);
}
