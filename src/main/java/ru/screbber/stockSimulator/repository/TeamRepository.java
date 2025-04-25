package ru.screbber.stockSimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.screbber.stockSimulator.entity.TeamEntity;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    List<TeamEntity> findByTournament_Id(Long tournamentId);

    Optional<TeamEntity> findByTournament_IdAndNameIgnoreCase(Long tournamentId, String name);

    long countByTournament_Id(Long tournamentId);
}
