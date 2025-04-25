package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.screbber.stockSimulator.entity.ParticipationEntity;
import ru.screbber.stockSimulator.service.TeamService;
import ru.screbber.stockSimulator.service.TournamentService;

@Controller
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TournamentService tournamentService;

    /** Создать команду в конкретном турнире */
    @PostMapping("/create")
    public String createTeam(@RequestParam Long tournamentId,
                             @RequestParam String name) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long participationId = tournamentService
                .getParticipationIdByUsernameAndTournamentId(username, tournamentId);

        ParticipationEntity part = tournamentService.getParticipationById(participationId);

        teamService.createTeam(tournamentId, name, part);
        return "redirect:/tournament/" + tournamentId;
    }

    /** Вступить в уже существующую команду */
    @PostMapping("/join")
    public String joinTeam(@RequestParam Long tournamentId,
                           @RequestParam Long teamId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long participationId = tournamentService
                .getParticipationIdByUsernameAndTournamentId(username, tournamentId);

        ParticipationEntity part = tournamentService.getParticipationById(participationId);
        teamService.joinTeam(teamId, part);

        return "redirect:/tournament/" + tournamentId;
    }
}
