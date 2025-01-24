package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.dto.StockPositionDto;
import ru.screbber.stockSimulator.entity.ParticipationEntity;
import ru.screbber.stockSimulator.entity.StockPositionEntity;
import ru.screbber.stockSimulator.exception.InsufficientBalanceException;
import ru.screbber.stockSimulator.exception.NoSuchTickerInYourPortfolioException;
import ru.screbber.stockSimulator.exception.NotEnoughStocksToSellException;
import ru.screbber.stockSimulator.exception.ParticipationNotFound;
import ru.screbber.stockSimulator.repository.ParticipationRepository;
import ru.screbber.stockSimulator.service.StockSourceService;
import ru.screbber.stockSimulator.service.StockTradingService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockTradingServiceImpl implements StockTradingService {

    private final ParticipationRepository participationRepository;

    private final StockSourceService stockSourceService;

    // TODO: подумать про индекс над тикером
    @Override
    public void buyStock(Long participationId, String ticker, int quantity) {
        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationNotFound("Participation not found"));

        BigDecimal stockPrice = stockSourceService.getStockPriceByTicker(ticker);
        BigDecimal totalPrice = stockPrice.multiply(BigDecimal.valueOf(quantity));

        // 1. Проверяем баланс
        if (participation.getBalance().compareTo(totalPrice) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in tournament");
        }

        // 2. Списываем деньги
        participation.setBalance(participation.getBalance().subtract(totalPrice));

        // 3. Пытаемся найти существующую позицию
        StockPositionEntity position = participation.getStockPositions().stream()
                .filter(stock -> stock.getTicker().equals(ticker))
                .findFirst()
                .orElse(null);

        if (position == null) {
            position = new StockPositionEntity();
            position.setTicker(ticker);
            position.setQuantity(0);
            position.setPurchaseAveragePrice(BigDecimal.ZERO);
            position.setParticipation(participation);
            participation.getStockPositions().add(position);
        }

        // 4. Пересчитываем среднюю цену (purchaseAveragePrice)
        int oldQuantity = position.getQuantity();
        BigDecimal oldAvgPrince = position.getPurchaseAveragePrice();
        int newQuantity = oldQuantity + quantity;

        if (oldQuantity == 0) {
            position.setPurchaseAveragePrice(stockPrice);
        } else {
            BigDecimal oldTotalCost = oldAvgPrince.multiply(BigDecimal.valueOf(oldQuantity));
            BigDecimal average = (oldTotalCost.add(totalPrice))
                    .divide(BigDecimal.valueOf(newQuantity), 2, RoundingMode.HALF_UP);

            position.setPurchaseAveragePrice(average);
        }

        position.setQuantity(newQuantity);
        participationRepository.save(participation);
    }

    @Override
    public void sellStock(Long participationId, String ticker, int quantity) {
        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationNotFound("Participation not found"));

        BigDecimal currentPrice = stockSourceService.getStockPriceByTicker(ticker);
        BigDecimal totalPrice = currentPrice.multiply(BigDecimal.valueOf(quantity));

        StockPositionEntity position = participation.getStockPositions().stream()
                .filter(p -> p.getTicker().equalsIgnoreCase(ticker))
                .findFirst()
                .orElseThrow(() -> new NoSuchTickerInYourPortfolioException("No such ticker in your portfolio"));

        if (position.getQuantity() < quantity) {
            throw new NotEnoughStocksToSellException("Not enough stocks to sell");
        }

        participation.setBalance(participation.getBalance().add(totalPrice));

        int newQuantity = position.getQuantity() - quantity;
        position.setQuantity(newQuantity);

        if (newQuantity == 0) {
            participation.getStockPositions().remove(position);
        }

        participationRepository.save(participation);
    }

    @Override
    public List<StockPositionDto> getUserStockPositions(Long participationId) {
        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));

        // Проходимся по всем StockPositionEntity и собираем DTO
        List<StockPositionEntity> positions = participation.getStockPositions();
        List<StockPositionDto> result = new ArrayList<>();

        for (StockPositionEntity pos : positions) {
            String ticker = pos.getTicker();
            BigDecimal purchasePrice = pos.getPurchaseAveragePrice();
            int quantity = pos.getQuantity();

            // Получаем данные с рынка
            // var details = stockSourceService.getStockDetails(ticker);
            BigDecimal stockPriceByTicker = stockSourceService.getStockPriceByTicker(ticker);


            StockPositionDto dto = new StockPositionDto();
            dto.setTicker(ticker);
            // dto.setDescription(details.getName());
            //dto.setCurrentPrice(details.getCurrentPrice());
            // dto.setTodayChange(details.getTodayChange());
            // dto.setTodayChangePct(details.getTodayChangePercent());

            dto.setDescription("some description");
            dto.setCurrentPrice(stockPriceByTicker);
            dto.setTodayChange(BigDecimal.valueOf(1));
            dto.setTodayChangePct(BigDecimal.valueOf(0.1));
            dto.setPurchasePrice(purchasePrice);
            dto.setQuantity(quantity);

            // Рассчитываем totalValue, gainLoss и т.д.
            if (dto.getCurrentPrice() != null) {
                dto.setTotalValue(dto.getCurrentPrice().multiply(BigDecimal.valueOf(quantity)));
                BigDecimal diff = dto.getCurrentPrice().subtract(purchasePrice);
                BigDecimal gainLoss = diff.multiply(BigDecimal.valueOf(quantity));
                dto.setTotalGainLoss(gainLoss);

                // (gainLoss / (purchasePrice*qty)) * 100
                BigDecimal cost = purchasePrice.multiply(BigDecimal.valueOf(quantity));
                if (cost.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal gainLossPct = gainLoss.divide(cost, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    dto.setTotalGainLossPercent(gainLossPct);
                } else {
                    dto.setTotalGainLossPercent(BigDecimal.ZERO);
                }
            } else {
                dto.setTotalValue(BigDecimal.ZERO);
                dto.setTotalGainLoss(BigDecimal.ZERO);
                dto.setTotalGainLossPercent(BigDecimal.ZERO);
            }
            result.add(dto);
        }
        return result;
    }
}
