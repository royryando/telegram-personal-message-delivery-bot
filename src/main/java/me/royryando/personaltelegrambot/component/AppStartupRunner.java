package me.royryando.personaltelegrambot.component;

import lombok.RequiredArgsConstructor;
import me.royryando.personaltelegrambot.bot.TelegramBot;
import me.royryando.personaltelegrambot.service.TelegramBotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
public class AppStartupRunner implements ApplicationRunner {
    private final TelegramClient telegramClient;
    private final TelegramBotService telegramBotService;

    @Value("${personaltelegrambot.telegram.bot.token}")
    private String token;

    @Override
    public void run(ApplicationArguments args) {
        try {
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(token, new TelegramBot(telegramBotService));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
