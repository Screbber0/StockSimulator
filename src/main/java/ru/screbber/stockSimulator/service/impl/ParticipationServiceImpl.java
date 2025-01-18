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
import ru.screbber.stockSimulator.service.ParticipationService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;

    private final UserRepository userRepository;

    private final TournamentRepository tournamentRepository;

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

}
