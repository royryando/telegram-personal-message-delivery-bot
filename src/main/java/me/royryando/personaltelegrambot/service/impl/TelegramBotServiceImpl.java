package me.royryando.personaltelegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import me.royryando.personaltelegrambot.model.Chat;
import me.royryando.personaltelegrambot.repository.ChatRepository;
import me.royryando.personaltelegrambot.service.TelegramBotService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@EnableAsync
public class TelegramBotServiceImpl implements TelegramBotService {
    private final ChatRepository chatRepository;
    private final TelegramClient telegramClient;

    @Transactional
    @Override
    public Chat saveChat(Message message) {
        var check = chatRepository.findFirstByTelegramId(message.getChatId());
        System.out.println(check.isPresent());
        if (check.isPresent()) return check.get();

        var chat = Chat.builder()
                .telegramId(message.getChatId())
                .type(message.getChat().getType())
                .firstName(message.getChat().getFirstName())
                .lastName(message.getChat().getLastName())
                .userName(message.getChat().getUserName())
                .title(message.getChat().getTitle())
                .showRawMessage(false)
                .build();
        return chatRepository.save(chat);
    }

    @Transactional
    @Override
    public void onStart(Message message) {
        if (!checkIsAdmin(message)) return;
        var channelExist = chatRepository.findFirstByTelegramId(message.getChatId()).orElse(saveChat(message));
        String textMessage;
        boolean sendGuide = false;
        if (channelExist.getChannelId() == null) {
            sendGuide = true;
            textMessage = "This chat is not linked to any channel and will not receive any message";
        } else {
            textMessage = String.format("This chat is linked to a channel [%s]", channelExist.getChannelId());
        }
        SendMessage sendMessage = new SendMessage(String.valueOf(message.getChatId()), textMessage);
        try {
            telegramClient.execute(sendMessage);
            if (sendGuide) sendGuide(message);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    @Transactional
    @Override
    public void onRegisterChannel(Message message) {
        if (!checkIsAdmin(message)) return;
        String text = message.getText();
        var payload = text.split("/registerChannel ");
        if (payload.length > 1) {
            String channelId = payload[1];
            var chat = chatRepository.findFirstByTelegramId(message.getChatId()).orElse(saveChat(message));
            chat.setChannelId(channelId);
            chatRepository.save(chat);

            String textMessage = String.format("Successfully linked to a channel *%s*", channelId);
            SendMessage sendMessage = new SendMessage(String.valueOf(message.getChatId()), textMessage);
            sendMessage.enableMarkdown(true);
            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        } else {
            sendGuide(message);
        }
    }

    @Transactional
    @Override
    public void onRevokeChannel(Message message) {
        if (!checkIsAdmin(message)) return;
        var chat = chatRepository.findFirstByTelegramId(message.getChatId()).orElse(saveChat(message));
        String textMessage;

        if (chat.getChannelId() == null || chat.getChannelId().isEmpty()) {
            textMessage = "No channel currently linked, nothing to revoke";
        } else {
            textMessage = "Channel successfully revoked, you are now not linked to any channel";
            chat.setChannelId(null);
            chatRepository.save(chat);
        }

        SendMessage sendMessage = new SendMessage(String.valueOf(message.getChatId()), textMessage);
        sendMessage.enableMarkdown(true);
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    @Transactional
    @Override
    public void onSetRawMessage(Message message) {
        if (!checkIsAdmin(message)) return;
        String text = message.getText();
        var payload = text.split("/showRawMessage ");
        if (payload.length > 0) {
            boolean value = payload[1].equalsIgnoreCase("true");
            var chat = chatRepository.findFirstByTelegramId(message.getChatId()).orElse(saveChat(message));
            chat.setShowRawMessage(value);
            chatRepository.save(chat);

            String textMessage = String.format("Setting applied: Show raw message [%s]", value);
            SendMessage sendMessage = new SendMessage(String.valueOf(message.getChatId()), textMessage);
            sendMessage.enableMarkdown(true);
            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        } else {
            sendGuide(message);
        }
    }

    @Transactional(readOnly = true)
    @Override
    @Async
    public void sendChat(String channelId, String message, boolean markdown) {
        var chats = chatRepository.findAllByChannelId(channelId);
        if (chats != null) {
            chats.forEach(chat -> {
                StringBuilder sb = new StringBuilder();
                if (!chat.getShowRawMessage()) {
                    sb.append(".::New Message Received::.\n\n");
                    sb.append(String.format("Channel ID: %s", channelId));
                    sb.append("\n\n");
                    sb.append("Message:\n");
                }
                sb.append(message);

                SendMessage sendMessage = new SendMessage(String.valueOf(chat.getTelegramId()), sb.toString());
                sendMessage.enableMarkdown(markdown);
                try {
                    telegramClient.execute(sendMessage);
                    ;
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private void sendGuide(Message message) {
        String textMessage = String.format("""
                        - To link this %s you need to set the Channel ID first
                        Start by sending /registerChannel followed by your Channel ID
                                        
                        Example: `/registerChannel 0cd23eb0-1244-4db3-9782-bfc033b53d6c`
                                        
                        - To revoke the linked Channel
                        Use /revokeChannel followed by your Channel ID
                                        
                        Example: `/revokeChannel 0cd23eb0-1244-4db3-9782-bfc033b53d6c`
                                        
                        - To show raw message without any detail or pretext
                        Use /showRawMessage true|false
                                        
                        Example: `/showRawMessage true`
                                        
                        %s
                        """,
                message.getChat().getType().equals("group") ? "Group" : "Chat",
                message.getChat().getType().equals("group") ? "*Only Group Admin can link/revoke a channel*" : ""
        );
        SendMessage sendMessage = new SendMessage(String.valueOf(message.getChatId()), textMessage);
        sendMessage.enableMarkdown(true);
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    private boolean checkIsAdmin(Message message) {
        if (message.getChat().getType().equalsIgnoreCase("private")) return true;
        try {
            var chat = GetChatAdministrators.builder()
                    .chatId(message.getChatId())
                    .build();
            List<ChatMember> members = telegramClient.execute(chat);
            return members.stream()
                    .anyMatch(member -> member.getUser().getId().equals(message.getFrom().getId()));
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
