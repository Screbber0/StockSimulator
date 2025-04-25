package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.constants.TournamentMode;
import ru.screbber.stockSimulator.entity.ParticipationEntity;
import ru.screbber.stockSimulator.entity.TeamEntity;
import ru.screbber.stockSimulator.entity.TournamentEntity;
import ru.screbber.stockSimulator.repository.TeamRepository;
import ru.screbber.stockSimulator.repository.TournamentRepository;
import ru.screbber.stockSimulator.service.TeamService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepo;
    private final TournamentRepository tournamentRepo;

    @Override
    public TeamEntity createTeam(Long tournamentId, String teamName, ParticipationEntity captain) {

        TournamentEntity tournament = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Турнир не найден"));

        if (tournament.getMode() != TournamentMode.TEAM) {
            throw new IllegalStateException("В данном турнире команды не предусмотрены");
        }

        if (tournament.getMaxTeams() != null &&
                teamRepo.countByTournament_Id(tournamentId) >= tournament.getMaxTeams()) {
            throw new IllegalStateException("Достигнут лимит команд");
        }

        teamRepo.findByTournament_IdAndNameIgnoreCase(tournamentId, teamName)
                .ifPresent(t -> { throw new IllegalStateException("Команда с таким названием уже есть"); });

        TeamEntity team = new TeamEntity();
        team.setName(teamName);
        team.setTournament(tournament);
        team.getParticipants().add(captain);
        captain.setTeam(team);

        return teamRepo.save(team);
    }

    @Override
    public void joinTeam(Long teamId, ParticipationEntity participant) {

        TeamEntity team = teamRepo.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Команда не найдена"));

        participant.setTeam(team);
        teamRepo.save(team);
    }

    @Override
    public List<TeamEntity> getTeamsInTournament(Long tournamentId) {
        return teamRepo.findByTournament_Id(tournamentId);
    }
}

