//package et.com.gebeya.safaricom.coreservice.service;
//
//import org.springframework.http.*;
//import jakarta.ws.rs.core.MediaType;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//
//@Service
//@RequiredArgsConstructor
//public class GeminiApiService {
//
//    @Value("${gemini.api.key}")
//    private String apiKey;
//
//    private final RestTemplate restTemplate;
//
//
//
//
//    public String generateContent(String text) {
//        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(org.springframework.http.MediaType.valueOf(MediaType.APPLICATION_JSON));
//
//        String requestBody = "{\"text\":\"" + text + "\"}";
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        return restTemplate.postForObject(url, requestEntity, String.class);
//    }
//}