package me.royryando.personaltelegrambot.service;

import me.royryando.personaltelegrambot.model.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface TelegramBotService {
    Chat saveChat(Message message);
    void onStart(Message message);
    void onRegisterChannel(Message message);
    void onRevokeChannel(Message message);
    void onSetRawMessage(Message message);
    void sendChat(String channelId, String message, boolean markdown);
}
