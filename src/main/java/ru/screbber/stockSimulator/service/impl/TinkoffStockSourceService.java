package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.screbber.stockSimulator.exception.StockNotFoundException;
import ru.screbber.stockSimulator.service.StockSourceService;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;

import java.math.BigDecimal;
import java.util.List;

import static ru.screbber.stockSimulator.constants.StockConstants.MOEX;
import static ru.tinkoff.piapi.core.utils.DateUtils.timestampToString;
import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;

@Service
@RequiredArgsConstructor
public class TinkoffStockSourceService implements StockSourceService {

    private final InvestApi api;

    static final Logger log = LoggerFactory.getLogger(TinkoffStockSourceService.class);

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
}
