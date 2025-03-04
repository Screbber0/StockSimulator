package ru.screbber.stockSimulator.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.screbber.stockSimulator.constants.TournamentMode;
import ru.screbber.stockSimulator.entity.stock.StockEntity;

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

    @ManyToMany
    @JoinTable(
            name = "tournament_allowed_stocks",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id")
    )
    private List<StockEntity> allowedStocks = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamEntity> teams = new ArrayList<>();

    private Integer maxTeams;

    private Integer maxTeamSize;
}
