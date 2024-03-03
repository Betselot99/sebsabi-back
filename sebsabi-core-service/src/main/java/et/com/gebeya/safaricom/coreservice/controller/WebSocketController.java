package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.MessageDto;
import et.com.gebeya.safaricom.coreservice.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebSocketController {
    private final WebSocketService webSocketService;
    @PostMapping("/send-message")
    public void sendMessage(@RequestBody final MessageDto messageDto){
        webSocketService.notifyFrontend(messageDto.getMessageContent());
    }
    @PostMapping("/send-proposal")
    public void sendProposalMessage(@PathVariable Long userId, @RequestBody final MessageDto messageDto){
        webSocketService.notifyClients(userId,messageDto.getMessageContent());
    }

}
