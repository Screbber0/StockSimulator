package ru.screbber.stockSimulator.service;

import ru.screbber.stockSimulator.dto.ParticipationDto;

public interface ParticipationService {

    ParticipationDto joinTournament(String username, Long tournamentId) throws Exception;
}
