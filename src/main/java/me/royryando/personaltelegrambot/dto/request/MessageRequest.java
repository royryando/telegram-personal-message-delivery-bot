package me.royryando.personaltelegrambot.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MessageRequest {
    private String channelId;
    private String message;
    @Builder.Default
    private boolean markdown = false;
}
