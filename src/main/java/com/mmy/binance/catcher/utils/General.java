package com.mmy.binance.catcher.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmy.binance.catcher.model.app.GeneralStatusInformation;
import com.mmy.binance.catcher.model.binance.AccountInformation;
import com.mmy.binance.catcher.model.binance.TradeInformationModel;
import com.mmy.binance.catcher.wss.client.BinanceWebSocketClient;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class General {
    public static Map<String, BinanceWebSocketClient> cacheWebSocketClient = new HashMap<>();

    public static Map<String, GeneralStatusInformation> generalStatusInformationMap = new HashMap<>();
    public static final String secretKey = "5QpBODMbOPwfHGT7FK8FsKqrOSq8N3GR0aUQ2yKMKKUKIMoZDmtJYkp1XItxAK7a";
    public static BigDecimal willBuyAmount = new BigDecimal(100);
    public static final String API_KEY = "w5D2aP3NLYl0gNVoh0bgI9StSi6obUEsHWSucQTldGKbCdTp63S9vTHFuUlnYALG";

    public static void buyProcess(TradeInformationModel tradeInformationModel) throws NoSuchAlgorithmException, InvalidKeyException {

        //sellOrBuyCoin(tradeInformationModel.getSymbol(), price, "BUY");
        //sellProcess(tradeInformationModel);

        AccountInformation accountInformation = getAccountInfo();
        if(accountInformation != null){
            accountInformation.getBalances().parallelStream().filter(balance -> balance.getAsset().equals(tradeInformationModel.getSymbol())).findFirst();
        }


        generalStatusInformationMap.put(tradeInformationModel.getSymbol().toLowerCase(),
                GeneralStatusInformation.builder().purchasePrice(tradeInformationModel.getPrice()).isBuy(true).build());

        System.out.println("ALDIMMMM -> " + tradeInformationModel.getPrice().toString());

    }

    public static void sellProcess(TradeInformationModel tradeInformationModel) throws NoSuchAlgorithmException, InvalidKeyException {

        if(!generalStatusInformationMap.containsKey(tradeInformationModel.getSymbol().toLowerCase()))
            return;

        else if(generalStatusInformationMap.get(tradeInformationModel.getSymbol().toLowerCase()).isSell())
            return;

        BigDecimal targetPrice = tradeInformationModel.getPrice().multiply(new BigDecimal(1.3));

        if(tradeInformationModel.getPrice().compareTo(targetPrice) >= 0){
            cacheWebSocketClient.get(tradeInformationModel.getSymbol().toLowerCase()).close();
            System.out.println("SATTIMMMM -> " + tradeInformationModel.getPrice().toString());
            /*
            AccountInformation accountInformation = getAccountInfo();
            if(accountInformation != null){
                accountInformation.getBalances().parallelStream().filter(balance -> balance.getAsset().equals(tradeInformationModel.getSymbol())).findFirst();
            }
            */
            //sellOrBuyCoin(tradeInformationModel.getSymbol(), BigDecimal.valueOf(0), "SELL");
        }

    }


    public static void sellOrBuyCoin(String symbol, BigDecimal price, String process) throws NoSuchAlgorithmException, InvalidKeyException {
        String queryString = String.format("symbol=%s&side=%s&type=MARKET&quantity=%s&timestamp=%s&recvWindow=30000&newOrderRespType=ACK",symbol, process, price, System.currentTimeMillis());

        String encrypted = hmacSHA256(queryString, secretKey);

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("https://api.binance.com/api/v3/order/test?%s&signature=%s", queryString, encrypted);

        // Başlıkları (headers) oluştur
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", API_KEY); // Örnek: Authorization başlığı

        // HttpEntity ile başlıkları sar
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String responseBody = responseEntity.getBody();
            HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();

            if (statusCode.is2xxSuccessful()) {
                System.out.println("Success: " + responseBody);
            } else {
                System.out.println("Request failed with status code: " + statusCode);
            }

        } catch (HttpClientErrorException e) {
            // 4xx hataları için (istemci hataları)
            System.out.println("Client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            // 5xx hataları için (sunucu hataları)
            System.out.println("Server error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            // Diğer tüm RestTemplate ile ilgili hatalar
            System.out.println("Error occurred: " + e.getMessage());
        }

    }

    public static AccountInformation getAccountInfo() throws NoSuchAlgorithmException, InvalidKeyException {
        String queryString = String.format("recvWindow=30000&timestamp=%s&omitZeroBalances=true", System.currentTimeMillis());

        String encrypted = hmacSHA256(queryString, secretKey);

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("https://api.binance.com/api/v3/account??%s&signature=%s", queryString, encrypted);

        // Başlıkları (headers) oluştur
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", API_KEY); // Örnek: Authorization başlığı

        // HttpEntity ile başlıkları sar
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String responseBody = responseEntity.getBody();
            HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();

            if (statusCode.is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                System.out.println("Success: " + responseBody);

                return objectMapper.readValue(responseBody, AccountInformation.class);

            } else {
                System.out.println("Request failed with status code: " + statusCode);
            }

        } catch (HttpClientErrorException e) {
            // 4xx hataları için (istemci hataları)
            System.out.println("Client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            // 5xx hataları için (sunucu hataları)
            System.out.println("Server error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            // Diğer tüm RestTemplate ile ilgili hatalar
            System.out.println("Error occurred: " + e.getMessage());
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String hmacSHA256(String data, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secretKeySpec);

        byte[] hashBytes = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return HexUtils.toHexString(hashBytes);
    }


}
