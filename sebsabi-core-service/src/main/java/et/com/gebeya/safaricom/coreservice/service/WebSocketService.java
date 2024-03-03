package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.dto.responseDto.ResponseMessageDto;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.WsSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private WebSocketService(SimpMessagingTemplate messagingTemplate){
        this.simpMessagingTemplate=messagingTemplate;
    }

    public void notifyFrontend(final String message){
        ResponseMessageDto responseMessageDto=new ResponseMessageDto(message);
        simpMessagingTemplate.convertAndSend("/topic/messages",responseMessageDto);
    }
    public void notifyClients(final Long userId,final String message){
        ResponseMessageDto responseMessageDto=new ResponseMessageDto(message);
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(userId),"/topic/proposal-message",responseMessageDto);
    }


}
