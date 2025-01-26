package ru.screbber.stockSimulator.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.screbber.stockSimulator.constants.TournamentMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"participants"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tournament")
public class TournamentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal initialCapital;

    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)
    private TournamentMode mode;

    private Integer randomStocksCount;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipationEntity> participants = new ArrayList<>();
}
