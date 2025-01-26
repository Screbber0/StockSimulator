package ru.screbber.stockSimulator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.screbber.stockSimulator.entity.StockEntity;
import ru.screbber.stockSimulator.repository.StockRepository;
import ru.screbber.stockSimulator.service.ParseStockService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ParseMOEXStockService implements ParseStockService {

    private final StockRepository stockRepository;


    @Transactional
    public void processStockFile() throws Exception {
        List<StockEntity> stocksToSave = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("stocks.csv")),
                "Windows-1251"))) {
            // Пропускаем заголовок
            String line = reader.readLine(); // read header

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");

                // Проверяем SUPERTYPE
                if ("Акции".equals(columns[4])) {
                    StockEntity stock = new StockEntity();

                    stock.setTicker(columns[7]);

                    String emitentFullName = columns[11];

                    // 1) Пытаемся найти текст в тройных кавычках """..."""
                    Pattern tripleQuotesPattern = Pattern.compile("^\"{3}([^\"].*?)\"{3}");
                    Matcher tripleMatcher = tripleQuotesPattern.matcher(emitentFullName);

                    String emitentName;
                    if (tripleMatcher.find()) {
                        // Если нашли тройные кавычки, берём из них
                        emitentName = tripleMatcher.group(1);
                    } else {
                        // Иначе ищем первое встречающееся "внутреннее" выражение в двойных кавычках ""...""
                        Pattern doubleQuotesPattern = Pattern.compile(".*?\"{2}([^\"].*?)\"{2}.*");
                        Matcher doubleMatcher = doubleQuotesPattern.matcher(emitentFullName);

                        if (doubleMatcher.find()) {
                            emitentName = doubleMatcher.group(1);
                        } else {
                            // Если ничего не нашли, можно оставить как есть или обрезать внешние кавычки
                            // Тут выберем оставить оригинал
                            emitentName = emitentFullName;
                        }
                    }
                    stock.setEmitentName(emitentName);
                    stocksToSave.add(stock);
                }
            }
        }
        stockRepository.saveAll(stocksToSave);
    }

    @Override
    public List<String> findTickersByPrefix(String prefix) {
        return stockRepository.findByTickerStartingWith(prefix.toUpperCase()).stream()
                .map(StockEntity::getTicker)
                .limit(5)
                .toList();
    }
}
