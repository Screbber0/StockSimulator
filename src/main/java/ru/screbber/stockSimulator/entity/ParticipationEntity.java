package ru.screbber.stockSimulator.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Table(name = "participation")
public class ParticipationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    // @JsonBackReference
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    // TODO: подумать как это убрать
    // @JsonBackReference
    private TournamentEntity tournament;

    private BigDecimal balance = BigDecimal.ZERO;

    @ElementCollection
    @CollectionTable(name = "user_stocks", joinColumns = @JoinColumn(name = "participation_id"))
    @MapKeyColumn(name = "ticker")
    @Column(name = "quantity")
    private Map<String, Integer> stocks = new HashMap<>();
}
