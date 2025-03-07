package ru.screbber.stockSimulator.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ToString(exclude = "participation")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "participation_history")
public class ParticipationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ссылка на участие (user + tournament)
    @ManyToOne
    @JoinColumn(name = "participation_id", nullable = false)
    private ParticipationEntity participation;

    // Дата (или дата-время), когда мы зафиксировали баланс
    private LocalDate snapshotDate;

    // Итоговый баланс на момент снятия снимка
    private BigDecimal totalBalance;
}
