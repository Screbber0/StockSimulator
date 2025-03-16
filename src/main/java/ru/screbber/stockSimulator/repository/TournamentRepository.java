package ru.screbber.stockSimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.screbber.stockSimulator.entity.TournamentEntity;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {

    Optional<TournamentEntity> findByName(String name);

    List<TournamentEntity> findByNameStartsWith(String name);
}
