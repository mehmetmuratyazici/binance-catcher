package com.mmy.binance.catcher.wss.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmy.binance.catcher.model.binance.TradeInformationModel;
import com.mmy.binance.catcher.utils.General;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class BinanceWebSocketClient extends WebSocketClient {
    ObjectMapper objectMapper ;
    public BinanceWebSocketClient(URI serverUri) {
        super(serverUri);
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
    }

    @Override
    public void onMessage(String message) {

        System.out.println("Received message: " + message);


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
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
