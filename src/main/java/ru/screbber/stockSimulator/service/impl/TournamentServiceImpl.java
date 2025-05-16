package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.constants.TournamentMode;
import ru.screbber.stockSimulator.dto.*;
import ru.screbber.stockSimulator.entity.*;
import ru.screbber.stockSimulator.entity.stock.StockPositionEntity;
import ru.screbber.stockSimulator.exception.ParticipationNotFound;
import ru.screbber.stockSimulator.repository.*;
import ru.screbber.stockSimulator.service.StockSourceService;
import ru.screbber.stockSimulator.service.TournamentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentServiceImpl implements TournamentService {

    private final ParticipationRepository participationRepository;

    private final UserRepository userRepository;

    private final TournamentRepository tournamentRepository;

    private final ParticipationHistoryRepository participantHistoryRepository;

    private final StockSourceService stockSourceService;

    private final TeamRepository teamRepository;

    private final  MessageSource messageSource;

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
        tournament.setMaxTeams(dto.getMaxTeams());
        tournamentRepository.save(tournament);
        log.debug("Турнир '{}' создан пользователем '{}' ", dto.getName(), username);

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
    public List<RankingTeamDto> getTeamRankingList(Long tournamentId) {
        List<TeamEntity> teams = teamRepository.findByTournament_Id(tournamentId);

        List<RankingTeamDto> list = teams.stream()
                .map(team -> {
                    BigDecimal bal = calculateTeamBalance(team);
                    return new RankingTeamDto(null, team.getName(), bal);
                })
                .sorted((a, b) -> b.getBalance().compareTo(a.getBalance()))
                .collect(Collectors.toList());

        long pos = 1;
        for (RankingTeamDto dto : list) {
            dto.setRankPosition(pos++);
        }
        return list;
    }

    /**
     * Считает средний баланс всех участников команды.
     */
    private BigDecimal calculateTeamBalance(TeamEntity team) {
        if (team.getParticipants().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalBalance = BigDecimal.ZERO;
        for (ParticipationEntity p : team.getParticipants()) {
            BigDecimal pBalance = getUserTotalBalanceByParticipationId(p.getId());
            totalBalance = totalBalance.add(pBalance);
        }
        return totalBalance.divide(BigDecimal.valueOf(team.getParticipants().size()), 2, RoundingMode.HALF_UP);
    }

    @Override
    public TournamentEntity getTournamentById(Long tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
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
    public List<TournamentSuggestDto> findTickersByPrefix(String tournamentPrefix) {
        return tournamentRepository.findByNameStartsWith(tournamentPrefix).stream()
                .limit(5)
                .map(tournament -> {
                    String localizedMode = localizeTournamentMode(tournament.getMode());
                    return new TournamentSuggestDto(tournament.getName(), localizedMode);
                })
                .toList();
    }

    private String localizeTournamentMode(TournamentMode mode) {
        return messageSource.getMessage("tournament.mode." + mode.name(), null, Locale.getDefault());
    }

    @Override
    public ParticipationEntity getParticipationById(Long participationId) {
        return participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationNotFound("Participation not found"));
    }
}
