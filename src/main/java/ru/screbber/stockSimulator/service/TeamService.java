package ru.screbber.stockSimulator.service;

import ru.screbber.stockSimulator.entity.ParticipationEntity;
import ru.screbber.stockSimulator.entity.TeamEntity;

import java.util.List;

public interface TeamService {
    TeamEntity createTeam(Long tournamentId, String teamName, ParticipationEntity captain);
    void joinTeam(Long teamId, ParticipationEntity participant);
    List<TeamEntity> getTeamsInTournament(Long tournamentId);
}