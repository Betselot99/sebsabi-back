package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.service.GeminiApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core")
public class GeminiController {
    private final GeminiApiService geminiApiService;



    @PostMapping("/generateContent")
    public String generateContent(@RequestBody String text) {
        return geminiApiService.generateContent(text);
    }
}
