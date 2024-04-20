package me.royryando.personaltelegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import me.royryando.personaltelegrambot.dto.request.MessageRequest;
import me.royryando.personaltelegrambot.service.MessageService;
import me.royryando.personaltelegrambot.service.TelegramBotService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final TelegramBotService telegramBotService;

    @Override
    public void sendMessage(MessageRequest request) throws BadRequestException {
        if (request.getChannelId() == null || request.getChannelId().isEmpty()) {
            throw new BadRequestException("Channel ID is required");
        }
        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            throw new BadRequestException("Message is required");
        }
        telegramBotService.sendChat(request.getChannelId(), request.getMessage(), request.isMarkdown());
    }
}
