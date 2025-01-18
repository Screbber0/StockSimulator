package ru.screbber.stockSimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.screbber.stockSimulator.entity.ParticipationEntity;
import ru.screbber.stockSimulator.entity.TournamentEntity;
import ru.screbber.stockSimulator.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<ParticipationEntity, Long> {

    List<ParticipationEntity> findByUser(UserEntity user);

    Optional<ParticipationEntity> findByUserAndTournament(UserEntity user, TournamentEntity tournament);
}
