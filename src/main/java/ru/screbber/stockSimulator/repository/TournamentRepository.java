package ru.screbber.stockSimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.screbber.stockSimulator.entity.TournamentEntity;

public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {
}
