package ru.screbber.stockSimulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.screbber.stockSimulator.entity.stock.StockEntity;

import java.util.List;
import java.util.Optional;


public interface StockRepository extends JpaRepository<StockEntity, Long> {

    List<StockEntity> findByTickerStartingWith(String prefix);

    Optional<StockEntity> findByTickerIgnoreCase(String ticker);

    @Query("""
        SELECT s FROM StockEntity s
        WHERE UPPER(s.ticker) LIKE UPPER(CONCAT(:prefix, '%'))
           OR UPPER(s.emitentName) LIKE UPPER(CONCAT(:prefix, '%'))
       """)
    List<StockEntity> findByTickerOrEmitentNameStartingWith(@Param("prefix") String prefix);
}
