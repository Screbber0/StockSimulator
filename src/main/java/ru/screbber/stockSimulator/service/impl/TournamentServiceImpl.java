package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.entity.TournamentEntity;
import ru.screbber.stockSimulator.repository.TournamentRepository;
import ru.screbber.stockSimulator.service.TournamentService;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;

    @Override
    public TournamentEntity createTournament(String name) {
        TournamentEntity tournament = new TournamentEntity();
        tournament.setName(name);
        return tournamentRepository.save(tournament);
    }
}
