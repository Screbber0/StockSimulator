package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.dto.*;
import ru.screbber.stockSimulator.entity.ParticipationEntity;
import ru.screbber.stockSimulator.entity.TournamentEntity;
import ru.screbber.stockSimulator.entity.UserEntity;
import ru.screbber.stockSimulator.entity.stock.StockPositionEntity;
import ru.screbber.stockSimulator.repository.ParticipationRepository;
import ru.screbber.stockSimulator.repository.TournamentRepository;
import ru.screbber.stockSimulator.repository.UserRepository;
import ru.screbber.stockSimulator.service.StockSourceService;
import ru.screbber.stockSimulator.service.TournamentService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final ParticipationRepository participationRepository;

    private final UserRepository userRepository;

    private final TournamentRepository tournamentRepository;

    private final StockSourceService stockSourceService;

    @Override
    public void createTournament(CreateTournamentDto dto) {
        TournamentEntity tournament = new TournamentEntity();
        tournament.setName(dto.getName());
        tournament.setStartDate(dto.getStartDate());
        tournament.setEndDate(dto.getEndDate());
        tournament.setInitialCapital(dto.getInitialCapital());
        tournament.setMaxParticipants(dto.getMaxParticipants());
        tournament.setMode(dto.getTournamentMode());
        tournament.setRandomStocksCount(dto.getRandomStocksCount());

        tournamentRepository.save(tournament);
    }

    @Override
    public ParticipationDto joinTournament(String username, String tournamentName) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TournamentEntity tournament = tournamentRepository.findByName(tournamentName)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        ParticipationEntity participation = new ParticipationEntity();
        participation.setUser(user);
        participation.setTournament(tournament);
        participation.setCash(tournament.getInitialCapital());
        ParticipationEntity savedParticipation = participationRepository.save(participation);

        // Преобразуем сущности в DTO
        UserDto userDto = new UserDto(user.getId(), user.getUsername());
        TournamentDto tournamentDto = new TournamentDto(tournament.getId(), tournament.getName());

        return new ParticipationDto(
                savedParticipation.getId(),
                userDto,
                tournamentDto,
                savedParticipation.getCash()
        );
    }

    @Override
    public List<TournamentDto> getTournaments(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Находим все участия пользователя
        List<ParticipationEntity> participations = participationRepository.findByUser(user);

        // Преобразуем участие в DTO турниров
        return participations.stream()
                .map(participation -> {
                    TournamentEntity tournament = participation.getTournament();
                    return new TournamentDto(tournament.getId(), tournament.getName());
                })
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getUserCashByParticipationId(Long participationId) {
        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));
        return participation.getCash();
    }

    @Override
    public BigDecimal getUserTotalBalanceByParticipationIdAndUserStockPositionList(Long participationId, List<StockPositionDto> userStockPositions) {
        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));
        BigDecimal totalBalance = participation.getCash();

        for (StockPositionDto stockPosition : userStockPositions) {
            BigDecimal multiply = stockPosition.getCurrentPrice().multiply(new BigDecimal(stockPosition.getQuantity()));
            totalBalance = totalBalance.add(multiply);
        }

        return totalBalance;
    }


    public BigDecimal getUserTotalBalanceByParticipationId(Long participationId) {
        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));
        BigDecimal totalBalance = participation.getCash();

        List<StockPositionEntity> stockPositions = participation.getStockPositions();
        for (StockPositionEntity stockPosition : stockPositions) {
            BigDecimal currentPrice = stockSourceService.getStockPriceByTicker(stockPosition.getTicker());
            BigDecimal multiply = currentPrice.multiply(new BigDecimal(stockPosition.getQuantity()));
            totalBalance = totalBalance.add(multiply);
        }

        return totalBalance;
    }

    @Override
    public Long getParticipationIdByUsernameAndTournamentId(String username, Long tournamentId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParticipationEntity participation = participationRepository.findByUserAndTournament(user, tournamentRepository.findById(tournamentId)
                        .orElseThrow(() -> new RuntimeException("Tournament not found")))
                .orElseThrow(() -> new RuntimeException("Participation not found"));

        return participation.getId();
    }

    @Override
    public Long getRankingByParticipation(Long participationId) {
        // 1) Находим участие
        ParticipationEntity currentPart = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));

        // 2) Берём турнир
        TournamentEntity tournament = currentPart.getTournament();
        // 3) Все участники этого турнира
        List<ParticipationEntity> allParticipants = tournament.getParticipants();

        // 4) Считаем totalBalance для каждого участника
        List<BigDecimal> allBalances = new ArrayList<>();

        BigDecimal currentUserBalance = getUserTotalBalanceByParticipationId(participationId);

        // Собираем totalBalance всех
        for (ParticipationEntity p : allParticipants) {
            BigDecimal bal = getUserTotalBalanceByParticipationId(p.getId());
            allBalances.add(bal);
        }

        // 5) Считаем, сколько имеют баланс больше, чем у текущего
        long rank = allBalances.stream()
                .filter(b -> b.compareTo(currentUserBalance) > 0)
                .count();

        // Ранг = 1 + число участников с большим балансом
        return rank + 1;
    }

    @Override
    public List<String> findTickersByPrefix(String tournamentPrefix) {
        return tournamentRepository.findByNameStartsWith(tournamentPrefix).stream()
                .map(TournamentEntity::getName)
                .limit(5)
                .toList();
    }
}
