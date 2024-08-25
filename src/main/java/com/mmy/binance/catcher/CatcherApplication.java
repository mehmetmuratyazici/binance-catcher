package com.mmy.binance.catcher;

import com.mmy.binance.catcher.model.WebSocketInitialRequest;
import com.mmy.binance.catcher.service.BinanceWebSocketService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URISyntaxException;

@SpringBootApplication
public class CatcherApplication {

    @Autowired
    private BinanceWebSocketService binanceWebSocketService;

    public static void main(String[] args) {
        SpringApplication.run(CatcherApplication.class, args);

    }

    @PostConstruct
    private void init() throws URISyntaxException, InterruptedException {
        binanceWebSocketService.startWebSocketConnection(new WebSocketInitialRequest("avaxusdt"));
    }
}
