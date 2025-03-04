package ru.screbber.stockSimulator.entity.stock;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import ru.screbber.stockSimulator.entity.ParticipationEntity;

import java.math.BigDecimal;

@Data
@ToString(exclude = {"participation"})
@Entity
@Table(name = "stock_position")
public class StockPositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String ticker;

    private Integer quantity;

    private BigDecimal purchaseAveragePrice;

    @ManyToOne()
    @JoinColumn(name = "participation_id", nullable = false)
    @JsonBackReference
    ParticipationEntity participation;
}
