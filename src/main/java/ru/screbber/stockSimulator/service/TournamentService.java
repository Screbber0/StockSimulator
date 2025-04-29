package ru.screbber.stockSimulator.service;

import ru.screbber.stockSimulator.dto.*;
import ru.screbber.stockSimulator.entity.ParticipationEntity;
import ru.screbber.stockSimulator.entity.TournamentEntity;

import java.math.BigDecimal;
import java.util.List;

public interface TournamentService {

    void createTournamentAndJoin(CreateTournamentDto createTournamentDto, String username);

    ParticipationDto joinTournament(String username, String tournamentName);

    List<TournamentDto> getTournaments(String username);

    BigDecimal getUserCashByParticipationId(Long participationId);

    BigDecimal getUserTotalBalanceByParticipationIdAndUserStockPositionList(Long participationId, List<ParticipantStockPositionDto> userStockPositions);

    BigDecimal getUserTotalBalanceByParticipationId(Long participationId);

    Long getParticipationIdByUsernameAndTournamentId(String username, Long tournamentId);

    Long getRankingByParticipation(Long participationId);

    List<RankingParticipantDto> getTournamentRankingList(Long tournamentId);

    List<RankingTeamDto> getTeamRankingList(Long teamId);

    List<ParticipationHistoryPointDto> getParticipationHistory(Long participationId);

    /**
     * Найти турнир по префиксу
     */
    List<TournamentSuggestDto> findTickersByPrefix(String tournamentPrefix);

    ParticipationEntity getParticipationById(Long participationId);

    TournamentEntity getTournamentById(Long tournamentId);
}
