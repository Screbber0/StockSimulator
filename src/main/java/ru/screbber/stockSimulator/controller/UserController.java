package ru.screbber.stockSimulator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.screbber.stockSimulator.dto.ParticipationDto;
import ru.screbber.stockSimulator.service.ParticipationService;

@RestController
@RequestMapping("/api/participation")
@RequiredArgsConstructor
public class UserController {


    private final ParticipationService participationService;

    @PostMapping("/join")
    public ParticipationDto joinTournament(@RequestParam String username, @RequestParam Long tournamentId) throws Exception {
        return participationService.joinTournament(username, tournamentId);
    }
}
