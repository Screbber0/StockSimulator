package ru.screbber.stockSimulator.exception;

public class NotEnoughStocksToSellException extends RuntimeException {

    public NotEnoughStocksToSellException(String message) {
        super(message);
    }
}
