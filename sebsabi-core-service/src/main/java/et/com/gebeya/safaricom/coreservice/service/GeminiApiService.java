//package et.com.gebeya.safaricom.coreservice.service;
//
//import jakarta.ws.rs.core.Cookie;
//import jakarta.ws.rs.core.HttpHeaders;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.MultivaluedMap;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.http.*;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//
//@Service
//public class GeminiApiService {
//
//    private final RestTemplate restTemplate;
//    private final String apiKey = "AIzaSyAxqfw_XAXCF3R6-63tguEX2Inmak0GQn0"; // Ensure this is stored securely
//    private final String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
//
//    public GeminiApiService() {
//        this.restTemplate = new RestTemplate();
//    }
//
//    public String sendPrompt(String prompt) {
//        HttpHeaders headers = new HttpHeaders() {
//            @Override
//            public List<String> getRequestHeader(String s) {
//                return null;
//            }
//
//            @Override
//            public String getHeaderString(String s) {
//                return null;
//            }
//
//            @Override
//            public MultivaluedMap<String, String> getRequestHeaders() {
//                return null;
//            }
//
//            @Override
//            public List<MediaType> getAcceptableMediaTypes() {
//                return null;
//            }
//
//            @Override
//            public List<Locale> getAcceptableLanguages() {
//                return null;
//            }
//
//            @Override
//            public MediaType getMediaType() {
//                return null;
//            }
//
//            @Override
//            public Locale getLanguage() {
//                return null;
//            }
//
//            @Override
//            public Map<String, Cookie> getCookies() {
//                return null;
//            }
//
//            @Override
//            public Date getDate() {
//                return null;
//            }
//
//            @Override
//            public int getLength() {
//                return 0;
//            }
//        };
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        String requestBody = "{\"contents\": [{\"parts\":[{\"text\": \"" + prompt + "\"}]}]}";
//
//        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//
//        // API key is added directly to the URL query parameters
//        ResponseEntity<String> response = restTemplate.postForEntity(geminiApiUrl + "?key=" + apiKey, request, String.class);
//        return response.getBody();
//    }
//}