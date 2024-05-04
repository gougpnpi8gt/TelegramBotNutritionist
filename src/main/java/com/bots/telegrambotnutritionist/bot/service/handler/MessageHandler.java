package com.bots.telegrambotnutritionist.bot.service.handler;

import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageHandler {
    final PersonRepository personRepository;

    @Autowired
    public MessageHandler(
                          PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
//        var user = personRepository.findUserByChatId(message.getChatId());
//        switch (user.getAction()) {
//            case SENDING_TOKEN -> {
//                return null;
//            }
//            case SENDING_DESCRIPTION,
//                    SENDING_TITTLE -> {
//                return null;
//            }
//            case SENDING_TASK,
//                    SENDING_MEDIA,
//                    SENDING_TEXT -> {
//                return null;
//            }
//        }
        return null;
    }
}
