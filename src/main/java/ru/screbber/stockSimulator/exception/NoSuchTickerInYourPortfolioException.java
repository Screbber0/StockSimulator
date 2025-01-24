package ru.screbber.stockSimulator.exception;

public class NoSuchTickerInYourPortfolioException extends RuntimeException {

    public NoSuchTickerInYourPortfolioException(String message) {
        super(message);
    }
}
