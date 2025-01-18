package ru.screbber.stockSimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.screbber.stockSimulator.entity.ParticipationEntity;

public interface ParticipationRepository extends JpaRepository<ParticipationEntity, Long> {
}
