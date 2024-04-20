package me.royryando.personaltelegrambot.repository;

import me.royryando.personaltelegrambot.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, BigInteger> {
    Optional<Chat> findFirstByTelegramId(Long telegramId);
    List<Chat> findAllByChannelId(String channelId);
}
