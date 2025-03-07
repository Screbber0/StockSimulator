package ru.screbber.stockSimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.screbber.stockSimulator.entity.ParticipationHistoryEntity;

import java.time.LocalDate;
import java.util.List;

public interface ParticipationHistoryRepository extends JpaRepository<ParticipationHistoryEntity, Long> {

    // История для конкретного участия, упорядочена по дате
    List<ParticipationHistoryEntity> findByParticipationIdOrderBySnapshotDateAsc(Long participationId);

    // При необходимости, за конкретный период:
    List<ParticipationHistoryEntity> findByParticipationIdAndSnapshotDateBetweenOrderBySnapshotDateAsc(
            Long participationId, LocalDate startDate, LocalDate endDate
    );
}
