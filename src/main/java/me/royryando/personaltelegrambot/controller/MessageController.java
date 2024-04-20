package me.royryando.personaltelegrambot.controller;

import lombok.RequiredArgsConstructor;
import me.royryando.personaltelegrambot.dto.request.MessageRequest;
import me.royryando.personaltelegrambot.service.MessageService;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public void sendMessage(@RequestBody MessageRequest request) throws BadRequestException {
        System.out.printf("Sending message: %s%n", request.toString());
        messageService.sendMessage(request);
    }

}
