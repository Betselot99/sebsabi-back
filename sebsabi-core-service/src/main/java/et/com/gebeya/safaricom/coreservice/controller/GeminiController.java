package et.com.gebeya.safaricom.coreservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.client.AiClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core")
public class GeminiController {
    private final AiClient aiClient;


    @GetMapping("/calculate")
    public String mathsQuetsions(){
        String prompt="whats is 1+2 ?";
        return aiClient.generate(prompt);

    }


}
