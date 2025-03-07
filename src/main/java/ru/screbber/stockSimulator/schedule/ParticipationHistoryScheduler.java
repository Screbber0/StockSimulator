package ru.screbber.stockSimulator.schedule;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.entity.ParticipationEntity;
import ru.screbber.stockSimulator.entity.ParticipationHistoryEntity;
import ru.screbber.stockSimulator.repository.ParticipationHistoryRepository;
import ru.screbber.stockSimulator.repository.ParticipationRepository;
import ru.screbber.stockSimulator.service.TournamentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationHistoryScheduler {

    private final ParticipationRepository participationRepository;
    private final ParticipationHistoryRepository historyRepository;
    private final TournamentService tournamentService;

    // Запускается каждый день в 00:00 - @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    @Scheduled(cron = "0 */1 * * * ?")
    public void saveDailyParticipationHistory() {
        System.out.println("=== Saving daily participation history ===");

        List<ParticipationEntity> allParticipation = participationRepository.findAll();

        for (ParticipationEntity part : allParticipation) {
            BigDecimal dailyBalance = tournamentService.getUserTotalBalanceByParticipationId(part.getId());
            ParticipationHistoryEntity history = new ParticipationHistoryEntity();
            history.setParticipation(part);
            history.setSnapshotDate(LocalDate.now());
            history.setTotalBalance(dailyBalance);

            historyRepository.save(history);
        }
    }
}
