package me.royryando.personaltelegrambot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chats")
public class Chat extends BaseEntity {
    private Long telegramId;
    private String channelId;
    private String type;
    private String title;
    private String firstName;
    private String lastName;
    private String userName;
    private Boolean showRawMessage;
}
