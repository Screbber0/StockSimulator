package ru.screbber.stockSimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.screbber.stockSimulator.entity.StockEntity;

import java.util.List;


public interface StockRepository extends JpaRepository<StockEntity, Long> {

    List<StockEntity> findStockEntitiesByTickerStartingWith(String prefix);
}
