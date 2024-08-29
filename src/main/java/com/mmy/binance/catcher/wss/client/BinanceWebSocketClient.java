package com.mmy.binance.catcher.wss.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmy.binance.catcher.model.binance.TradeInformationModel;
import com.mmy.binance.catcher.utils.General;
import jakarta.websocket.ClientEndpoint;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

@ClientEndpoint
public class BinanceWebSocketClient extends WebSocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BinanceWebSocketClient.class);

    ObjectMapper objectMapper ;
    public BinanceWebSocketClient(URI serverUri) {
        super(serverUri);
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                sendPing();
            }
        };

        timer.scheduleAtFixedRate(task, 0, 300000);
    }

    @Override
    public void onMessage(String message) {

        System.out.println(System.currentTimeMillis() + " " +"Received message: " + message);
        Thread.ofVirtual().start(() -> {
            try {
                TradeInformationModel tradeInformationModel = objectMapper.readValue(message, TradeInformationModel.class);

                if(General.cacheCoinPrices.get(tradeInformationModel.getSymbol().toLowerCase()).compareTo(tradeInformationModel.getPrice()) != 0){
                    General.cacheCoinPrices.put(tradeInformationModel.getSymbol().toLowerCase(), tradeInformationModel.getPrice());
                    LOGGER.info("Changed price -> newPrice : " + tradeInformationModel.getPrice().toString());
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });
        /*

        try {
            TradeInformationModel tradeInformationModel = objectMapper.readValue(message, TradeInformationModel.class);

            if(!General.generalStatusInformationMap.containsKey(tradeInformationModel.getSymbol().toLowerCase()) || !General.generalStatusInformationMap.get(tradeInformationModel.getSymbol().toLowerCase()).isBuy()){
                General.buyProcess(tradeInformationModel);
            }
            else {
                General.sellProcess(tradeInformationModel);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println(String.format("Connection closed -> code: %s, reason: %s" , code, reason));
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }


}
