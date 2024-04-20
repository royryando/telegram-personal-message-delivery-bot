package me.royryando.personaltelegrambot.service;

import me.royryando.personaltelegrambot.dto.request.MessageRequest;
import org.apache.coyote.BadRequestException;

public interface MessageService {
    void sendMessage(MessageRequest request) throws BadRequestException;
}
