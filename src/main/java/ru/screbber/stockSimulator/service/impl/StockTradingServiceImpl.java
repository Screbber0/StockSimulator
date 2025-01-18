package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.entity.ParticipationEntity;
import ru.screbber.stockSimulator.repository.ParticipationRepository;
import ru.screbber.stockSimulator.service.StockService;
import ru.screbber.stockSimulator.service.StockTradingService;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockTradingServiceImpl implements StockTradingService {

    private final ParticipationRepository participationRepository;

    private final StockService stockService;

    public void buyStock(Long participationId, String ticker, int quantity) throws Exception {
        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new Exception("Participation not found"));

        BigDecimal stockPrice = stockService.getStockPriceByTicker(ticker);
        BigDecimal totalPrice = stockPrice.multiply(BigDecimal.valueOf(quantity));

        if (participation.getBalance().compareTo(totalPrice) < 0) {
            throw new Exception("Insufficient balance in tournament");
        }

        participation.setBalance(participation.getBalance().subtract(totalPrice));
        participation.getStocks().put(ticker, participation.getStocks().getOrDefault(ticker, 0) + quantity);

        participationRepository.save(participation);
    }

    public void sellStock(Long participationId, String ticker, int quantity) throws Exception {
        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new Exception("Participation not found"));

        Integer ownedQuantity = participation.getStocks().getOrDefault(ticker, 0);

        if (ownedQuantity < quantity) {
            throw new Exception("Not enough stocks to sell");
        }

        BigDecimal stockPrice = stockService.getStockPriceByTicker(ticker);
        BigDecimal totalPrice = stockPrice.multiply(BigDecimal.valueOf(quantity));

        participation.setBalance(participation.getBalance().add(totalPrice));
        participation.getStocks().put(ticker, ownedQuantity - quantity);

        if (participation.getStocks().get(ticker) == 0) {
            participation.getStocks().remove(ticker);
        }

        participationRepository.save(participation);
    }

    public Map<String, Integer> getUserStocks(Long participationId) throws Exception {
        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new Exception("Participation not found"));

        return participation.getStocks();
    }
}
