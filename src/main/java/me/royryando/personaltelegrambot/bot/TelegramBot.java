package me.royryando.personaltelegrambot.bot;

import me.royryando.personaltelegrambot.service.TelegramBotService;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

public class TelegramBot implements LongPollingUpdateConsumer {
    private final TelegramBotService telegramBotService;

    public TelegramBot(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @Override
    public void consume(List<Update> list) {
        list.forEach(this::consume);
    }

    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            telegramBotService.saveChat(update.getMessage());
            if (update.getMessage().getText().startsWith("/")) {
                handleCommand(update.getMessage());
            }
        }
    }

    private void handleCommand(Message message) {
        try {
            String command = message.getText().contains(" ") ? message.getText().split(" ")[0] : message.getText();
            switch (command) {
                case "/start":
                    telegramBotService.onStart(message);
                    break;
                case "/registerChannel":
                    telegramBotService.onRegisterChannel(message);
                    break;
                case "/revokeChannel":
                    telegramBotService.onRevokeChannel(message);
                    break;
                case "/showRawMessage":
                    telegramBotService.onSetRawMessage(message);
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
