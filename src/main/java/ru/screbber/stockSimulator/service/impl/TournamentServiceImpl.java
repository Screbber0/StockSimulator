package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.dto.ParticipationDto;
import ru.screbber.stockSimulator.dto.TournamentDto;
import ru.screbber.stockSimulator.dto.UserDto;
import ru.screbber.stockSimulator.entity.ParticipationEntity;
import ru.screbber.stockSimulator.entity.TournamentEntity;
import ru.screbber.stockSimulator.entity.UserEntity;
import ru.screbber.stockSimulator.repository.ParticipationRepository;
import ru.screbber.stockSimulator.repository.TournamentRepository;
import ru.screbber.stockSimulator.repository.UserRepository;
import ru.screbber.stockSimulator.service.TournamentService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final ParticipationRepository participationRepository;

    private final UserRepository userRepository;

    private final TournamentRepository tournamentRepository;

    public void createTournament(String name) {
        TournamentEntity tournament = new TournamentEntity();
        tournament.setName(name);
        tournamentRepository.save(tournament);
    }

    @Override
    public ParticipationDto joinTournament(String username, Long tournamentId) throws Exception {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));

        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new Exception("Tournament not found"));

        ParticipationEntity participation = new ParticipationEntity();
        participation.setUser(user);
        participation.setTournament(tournament);
        participation.setBalance(BigDecimal.valueOf(10000));
        ParticipationEntity savedParticipation = participationRepository.save(participation);

        // Преобразуем сущности в DTO
        UserDto userDto = new UserDto(user.getId(), user.getUsername());
        TournamentDto tournamentDto = new TournamentDto(tournament.getId(), tournament.getName());

        return new ParticipationDto(
                savedParticipation.getId(),
                userDto,
                tournamentDto,
                savedParticipation.getBalance(),
                savedParticipation.getStocks()
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
    public BigDecimal getUserBalanceInTournament(String username, Long tournamentId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        ParticipationEntity participation = participationRepository.findByUserAndTournament(user, tournament)
                .orElseThrow(() -> new RuntimeException("Participation not found"));

        return participation.getBalance();
    }

    @Override
    public Long getParticipationByUsernameAndTournamentId(String username, Long tournamentId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParticipationEntity participation = participationRepository.findByUserAndTournament(user, tournamentRepository.findById(tournamentId)
                        .orElseThrow(() -> new RuntimeException("Tournament not found")))
                .orElseThrow(() -> new RuntimeException("Participation not found"));

        return participation.getId();
    }
}
