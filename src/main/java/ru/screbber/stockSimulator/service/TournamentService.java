package ru.screbber.stockSimulator.service;

import ru.screbber.stockSimulator.dto.CreateTournamentDto;
import ru.screbber.stockSimulator.dto.ParticipationDto;
import ru.screbber.stockSimulator.dto.StockPositionDto;
import ru.screbber.stockSimulator.dto.TournamentDto;

import java.math.BigDecimal;
import java.util.List;

public interface TournamentService {

    void createTournament(CreateTournamentDto dto);

    ParticipationDto joinTournament(String username, String tournamentName);

    List<TournamentDto> getTournaments(String username);

    BigDecimal getUserCashByParticipationId(Long participationId);

    BigDecimal getUserTotalBalanceByParticipationIdAndUserStockPositionList(Long participationId, List<StockPositionDto> userStockPositions);

    Long getParticipationIdByUsernameAndTournamentId(String username, Long tournamentId);

    Long getRankingByParticipation(Long participationId);

    /**
     * Найти турнир по префиксу
     */
    List<String> findTickersByPrefix(String tournamentPrefix);
}
