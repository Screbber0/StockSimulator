package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.dto.*;
import ru.screbber.stockSimulator.entity.ParticipationEntity;
import ru.screbber.stockSimulator.entity.ParticipationHistoryEntity;
import ru.screbber.stockSimulator.entity.TournamentEntity;
import ru.screbber.stockSimulator.entity.UserEntity;
import ru.screbber.stockSimulator.entity.stock.StockPositionEntity;
import ru.screbber.stockSimulator.repository.ParticipationHistoryRepository;
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

    private final ParticipationHistoryRepository participantHistoryRepository;

    private final StockSourceService stockSourceService;

    @Override
    public void createTournamentAndJoin(CreateTournamentDto dto, String username) {
        TournamentEntity tournament = new TournamentEntity();
        tournament.setName(dto.getName());
        tournament.setStartDate(dto.getStartDate());
        tournament.setEndDate(dto.getEndDate());
        tournament.setInitialCapital(dto.getInitialCapital());
        tournament.setMaxParticipants(dto.getMaxParticipants());
        tournament.setMode(dto.getTournamentMode());
        tournament.setRandomStocksCount(dto.getRandomStocksCount());
        tournamentRepository.save(tournament);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParticipationEntity participation = new ParticipationEntity();
        participation.setUser(user);
        participation.setTournament(tournament);
        participation.setCash(tournament.getInitialCapital());
        participationRepository.save(participation);
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
    public BigDecimal getUserTotalBalanceByParticipationIdAndUserStockPositionList(Long participationId, List<ParticipantStockPositionDto> participantStockPosition) {
        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));
        BigDecimal totalBalance = participation.getCash();

        for (ParticipantStockPositionDto stockPosition : participantStockPosition) {
            BigDecimal multiply = stockPosition.getCurrentPrice().multiply(BigDecimal.valueOf(stockPosition.getQuantity()));
            totalBalance = totalBalance.add(multiply);
        }

        return totalBalance;
    }

    @Override
    public BigDecimal getUserTotalBalanceByParticipationId(Long participationId) {
        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));
        BigDecimal totalBalance = participation.getCash();

        List<StockPositionEntity> stockPositions = participation.getStockPositions();
        for (StockPositionEntity stockPosition : stockPositions) {
            BigDecimal currentPrice = stockSourceService.getStockPriceByTicker(stockPosition.getTicker());
            totalBalance = totalBalance.add(currentPrice.multiply(BigDecimal.valueOf(stockPosition.getQuantity())));
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
        List<BigDecimal> allParticipantsTotalBalances = new ArrayList<>();

        BigDecimal currentUserTotalBalance = getUserTotalBalanceByParticipationId(participationId);

        // Собираем totalBalance всех
        for (ParticipationEntity p : allParticipants) {
            BigDecimal participantsTotalBalance = getUserTotalBalanceByParticipationId(p.getId());
            allParticipantsTotalBalances.add(participantsTotalBalance);
        }

        // 5) Считаем, сколько имеют баланс больше, чем у текущего
        long rank = allParticipantsTotalBalances.stream()
                .filter(b -> b.compareTo(currentUserTotalBalance) > 0)
                .count();

        // Ранг = 1 + число участников с большим балансом
        return rank + 1;
    }

    @Override
    public List<RankingParticipantDto> getTournamentRankingList(Long tournamentId) {
        // 1) Находим турнир
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // 2) Получаем всех участников
        List<ParticipationEntity> participants = tournament.getParticipants();

        // 3) Для каждого считаем totalBalance
        //    Потом сортируем по убыванию баланса
        List<RankingParticipantDto> rankingList = new ArrayList<>();
        for (ParticipationEntity p : participants) {
            BigDecimal balance = getUserTotalBalanceByParticipationId(p.getId());
            rankingList.add(new RankingParticipantDto(
                    p.getUser().getUsername(),
                    balance,
                    0L // пока ставим 0, позже назначим rankPosition
            ));
        }

        // Сортируем по балансу (desc)
        rankingList.sort((a, b) -> b.getTotalBalance().compareTo(a.getTotalBalance()));

        // 4) Проставляем rankPosition
        // Участник с самым большим балансом получает позицию 1, следующий — 2 и т. д.
        long pos = 1;
        for (RankingParticipantDto dto : rankingList) {
            dto.setRankPosition(pos);
            pos++;
        }

        return rankingList;
    }

    @Override
    public List<ParticipationHistoryPointDto> getParticipationHistory(Long participationId) {
        List<ParticipationHistoryEntity> records = participantHistoryRepository
                .findByParticipationIdOrderBySnapshotDateAsc(participationId);

        return records.stream()
                .map(r -> new ParticipationHistoryPointDto(
                        r.getSnapshotDate().toString(),
                        r.getTotalBalance()
                ))
                .collect(Collectors.toList());
    }



    @Override
    public List<String> findTickersByPrefix(String tournamentPrefix) {
        return tournamentRepository.findByNameStartsWith(tournamentPrefix).stream()
                .map(TournamentEntity::getName)
                .limit(5)
                .toList();
    }
}
