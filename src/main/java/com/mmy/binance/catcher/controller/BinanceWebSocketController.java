package com.mmy.binance.catcher.controller;

import com.mmy.binance.catcher.model.WebSocketInitialRequest;
import com.mmy.binance.catcher.model.WebSocketInitialResponse;
import com.mmy.binance.catcher.service.BinanceWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Set;

@RestController
@RequestMapping("websocket")
public class BinanceWebSocketController {

    @Autowired
    private BinanceWebSocketService binanceWebSocketService;

    @PostMapping
    public ResponseEntity<WebSocketInitialResponse> createNewConnection(@RequestBody WebSocketInitialRequest webSocketInitialRequest) throws URISyntaxException, InterruptedException {
        return ResponseEntity.ok(binanceWebSocketService.startWebSocketConnection(webSocketInitialRequest));
    }

    @GetMapping("close/{id}")
    public void closeWebSocketById(@PathVariable String id){
        binanceWebSocketService.closeWebSocket(id);
    }

    @GetMapping("startedWebSocketList")
    public ResponseEntity<Set<String>> startedWebSocketList(){
        return ResponseEntity.ok(binanceWebSocketService.getAllWebSocketId());
    }
}
