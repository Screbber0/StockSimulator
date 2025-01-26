package ru.screbber.stockSimulator.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"stockPositions"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "participation")
public class ParticipationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    private BigDecimal cash;

    @OneToMany(mappedBy = "participation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockPositionEntity> stockPositions = new ArrayList<>();
}
