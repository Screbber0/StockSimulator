package ru.screbber.stockSimulator.service;

import org.springframework.scheduling.annotation.Async;

public interface MailSenderService {

    @Async
    void sendAsync(String to, String subject, String body);
}
