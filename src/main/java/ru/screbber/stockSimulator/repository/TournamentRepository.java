package ru.screbber.stockSimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.screbber.stockSimulator.entity.TournamentEntity;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {

    Optional<TournamentEntity> findByName(String name);

    Optional<TournamentEntity> findByNameStartsWith(String name);
}
