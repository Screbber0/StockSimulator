package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.screbber.stockSimulator.entity.TournamentEntity;
import ru.screbber.stockSimulator.service.TournamentService;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping("/create")
    public TournamentEntity createTournament(@RequestParam String name) {
        try {
            return tournamentService.createTournament(name);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
