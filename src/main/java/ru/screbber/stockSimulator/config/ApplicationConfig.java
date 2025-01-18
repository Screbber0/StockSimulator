package ru.screbber.stockSimulator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.piapi.core.InvestApi;


@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    @Bean
    public InvestApi api() {

        String ssoToken = System.getenv("ssoToken");

        return InvestApi.create(ssoToken);
    }
}