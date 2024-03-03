package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.MessageDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.ResponseMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageController {
    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public ResponseMessageDto getMessage(final MessageDto messageDto) throws InterruptedException {
        Thread.sleep(1000);
        return new ResponseMessageDto(HtmlUtils.htmlEscape(messageDto.getMessageContent()));
    }
    @MessageMapping("/proposal-message")
    @SendToUser("/topic/proposal-message")
    public ResponseMessageDto getProposalMessage(Principal principal,final MessageDto messageDto) throws InterruptedException {
        Thread.sleep(1000);
        return new ResponseMessageDto(HtmlUtils.htmlEscape(
                "Sending Proposal Message to User"+principal.getName()+": "+messageDto.getMessageContent()));
    }



}
