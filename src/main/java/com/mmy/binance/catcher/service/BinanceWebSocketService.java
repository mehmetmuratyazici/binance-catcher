package com.mmy.binance.catcher.service;

import com.mmy.binance.catcher.model.WebSocketInitialRequest;
import com.mmy.binance.catcher.model.WebSocketInitialResponse;
import com.mmy.binance.catcher.utils.General;
import com.mmy.binance.catcher.wss.client.BinanceWebSocketClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class BinanceWebSocketService {
    //private Map<String, BinanceWebSocketClient> cacheWebSocketClient = new HashMap<>();
    @Value("${websocket.url}")
    private String webSocketUrl;

    private final String WEB_SOCKET_TRADE = "@aggTrade";

    public WebSocketInitialResponse startWebSocketConnection(WebSocketInitialRequest webSocketInitialRequest) throws URISyntaxException, InterruptedException {
        BinanceWebSocketClient binanceWebSocketClient = new BinanceWebSocketClient(new URI(webSocketUrl + webSocketInitialRequest.getSymbol() + WEB_SOCKET_TRADE));
        String id = null;
        if (binanceWebSocketClient.connectBlocking()) {
            id = webSocketInitialRequest.getSymbol();// + "_" + UUID.randomUUID();
            General.cacheWebSocketClient.put(id, binanceWebSocketClient);
            General.cacheCoinPrices.put(id, BigDecimal.ZERO);
        }

        return new WebSocketInitialResponse(id);
    }

    public void closeWebSocket(String id) {
        General.cacheWebSocketClient.get(id).close();
    }

    public Set<String> getAllWebSocketId() {
        return General.cacheWebSocketClient.keySet();
    }
}
