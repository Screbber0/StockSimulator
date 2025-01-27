package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.dto.StockInfoDto;
import ru.screbber.stockSimulator.exception.StockNotFoundException;
import ru.screbber.stockSimulator.service.StockSourceService;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

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
        // TODO: изменить весь метод
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

        // В реальном приложении здесь был бы вызов API или запрос к БД
        // Пока что делаем "заглушку".
        // Допустим, если пришло "SBER", вернём статические данные.
        // Иначе - тоже какие-то примерные данные.

        StockInfoDto dto = new StockInfoDto();

        // Убедимся, что по сути обрабатываем тикер в UpperCase
        String upperTicker = ticker.toUpperCase(Locale.ROOT);

        dto.setTicker(upperTicker);
        dto.setCurrentPrice(new BigDecimal("200.00"));
        dto.setChange(new BigDecimal("+2.50"));
        dto.setChangePercent(new BigDecimal("1.26"));
        dto.setDayHigh(new BigDecimal("205.00"));
        dto.setDayLow(new BigDecimal("195.00"));
        dto.setVolume(new BigDecimal("1000000"));

        switch (upperTicker) {
            case "AAPL":
                dto.setCompanyName("Apple Inc");
                break;
            case "TSLA":
                dto.setCompanyName("Tesla Inc");
                break;
            case "AMZN":
                dto.setCompanyName("Amazon.com, Inc.");
                break;
            // ... можно дописать другие тикеры
            default:
                dto.setCompanyName("Unknown NASDAQ stock");
                break;
        }
        return dto;
    }
}
