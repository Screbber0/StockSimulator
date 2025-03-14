package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.dto.StockCandlePointDto;
import ru.screbber.stockSimulator.dto.StockInfoDto;
import ru.screbber.stockSimulator.exception.StockNotFoundException;
import ru.screbber.stockSimulator.service.StockSourceService;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static ru.screbber.stockSimulator.constants.StockConstants.MOEX;
import static ru.tinkoff.piapi.core.utils.DateUtils.timestampToString;
import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;

@Service
@RequiredArgsConstructor
@EnableCaching
public class TinkoffStockSourceService implements StockSourceService {

    private final InvestApi api;

    static final Logger log = LoggerFactory.getLogger(TinkoffStockSourceService.class);

    @Override
    @Cacheable(value = "stockPrices", key = "#ticker.toUpperCase()")
    public BigDecimal getStockPriceByTicker(String ticker) {
        Share share = api.getInstrumentsService().getShareByTickerSync(ticker, MOEX.getValue());
        var lastPrices = api.getMarketDataService().getLastPricesSync(List.of(share.getFigi()));
        for (LastPrice lastPrice : lastPrices) {
            String figi = lastPrice.getFigi();
            BigDecimal price = quotationToBigDecimal(lastPrice.getPrice());
            String time = timestampToString(lastPrice.getTime());
            log.info("последняя цена по инструменту {}, цена: {}, время обновления цены: {}", figi, price, time);
            return price;
        }
        throw new StockNotFoundException("Акция не найдена");
    }

    @Override
    public StockInfoDto getStockInfo(String ticker) {

        // 1) Ищем Share по тикеру
        Share share = api.getInstrumentsService().getShareByTickerSync(ticker, MOEX.getValue());
        String figi = share.getFigi();

        // 2) Получаем "last price" (текущую цену)
        var lastPrices = api.getMarketDataService().getLastPricesSync(List.of(figi));
        BigDecimal currentPrice = BigDecimal.ZERO;
        if (!lastPrices.isEmpty()) {
            currentPrice = quotationToBigDecimal(lastPrices.get(0).getPrice());
        }

        // 3) Чтобы получить high/low/volume за день, грузим свечи с 00:00 сегодняшнего дня по now
        Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant now = Instant.now();

        List<HistoricCandle> dayCandles = api.getMarketDataService().getCandlesSync(
                figi,
                startOfDay,
                now,
                CandleInterval.CANDLE_INTERVAL_5_MIN
        );

        // 4) Вычисляем high/low/volume за день
        BigDecimal dayHigh = BigDecimal.ZERO;
        BigDecimal dayLow = null;
        long totalVolume = 0L;

        for (HistoricCandle c : dayCandles) {
            BigDecimal high = quotationToBigDecimal(c.getHigh());
            BigDecimal low  = quotationToBigDecimal(c.getLow());

            // max
            if (high.compareTo(dayHigh) > 0) {
                dayHigh = high;
            }
            // min
            if (dayLow == null || low.compareTo(dayLow) < 0) {
                dayLow = low;
            }
            // volume
            totalVolume += c.getVolume();
            // c.getVolume() – long, возвращает объём в «штуках» (лоты).
        }
        if (dayLow == null) {
            dayLow = currentPrice;
        }

        StockInfoDto dto = new StockInfoDto();
        dto.setTicker(share.getTicker());
        dto.setCompanyName(share.getName());
        dto.setCurrentPrice(currentPrice);
        dto.setDayHigh(dayHigh);
        dto.setDayLow(dayLow);
        dto.setVolume(BigDecimal.valueOf(totalVolume));

        Instant startOfYesterday = startOfDay.minus(1, ChronoUnit.DAYS);

        // Берём последние несколько минут за вчера, или целый день, в зависимости от логики
        List<HistoricCandle> yesterdayCandles = api.getMarketDataService().getCandlesSync(
                figi,
                startOfYesterday,
                startOfDay,
                CandleInterval.CANDLE_INTERVAL_5_MIN
        );

        // 2) Найти самую последнюю свечу из yesterdayCandles
        HistoricCandle lastCandleYesterday = null;
        if (!yesterdayCandles.isEmpty()) {
            lastCandleYesterday = yesterdayCandles.get(yesterdayCandles.size() - 1);
        }

        BigDecimal previousClosePrice = null;
        if (lastCandleYesterday != null) {
            previousClosePrice = quotationToBigDecimal(lastCandleYesterday.getClose());
        }

        // 3) Если нашли previousClosePrice, считаем change/percent
        if (previousClosePrice != null && previousClosePrice.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal change = currentPrice.subtract(previousClosePrice);
            BigDecimal changePercent = change
                    .divide(previousClosePrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            dto.setChange(change);
            dto.setChangePercent(changePercent);
        } else {
            // Если не нашли, то ставим 0
            dto.setChange(BigDecimal.ZERO);
            dto.setChangePercent(BigDecimal.ZERO);
        }

        return dto;
    }

    @Override
    public List<StockCandlePointDto> getHistoricPrices(String ticker, Instant from, Instant to) {

        // 1) Получаем Share
        Share share = api.getInstrumentsService().getShareByTickerSync(ticker, MOEX.getValue());
        if (share == null) {
            throw new StockNotFoundException("No share found for ticker = " + ticker);
        }
        String figi = share.getFigi();

        // 2) Загружаем свечи
        List<HistoricCandle> candles;
        try {
            candles = api.getMarketDataService().getCandlesSync(
                    figi,
                    from,
                    to,
                    CandleInterval.CANDLE_INTERVAL_2_HOUR
            );
        } catch (ApiRuntimeException e) {
            throw new RuntimeException("Error fetching candles from Tinkoff: " + e.getMessage(), e);
        }

        List<StockCandlePointDto> result = new ArrayList<>();
        for (HistoricCandle c : candles) {
            com.google.protobuf.Timestamp protoTs = c.getTime();
            Instant candleTime = Instant.ofEpochSecond(protoTs.getSeconds(), protoTs.getNanos());
            BigDecimal open = quotationToBigDecimal(c.getOpen());
            BigDecimal close = quotationToBigDecimal(c.getClose());
            BigDecimal high = quotationToBigDecimal(c.getHigh());
            BigDecimal low = quotationToBigDecimal(c.getLow());

            result.add(new StockCandlePointDto(candleTime, open, high, low, close));
        }
        return result;
    }
}
