package com.bots.telegrambotnutritionist.bot.service.handler;

import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.service.manager.review.ReviewManager;
import com.bots.telegrambotnutritionist.bot.service.manager.submit.SubmitManager;
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
    final SubmitManager submitManager;
    final ReviewManager reviewManager;

    @Autowired
    public MessageHandler(PersonRepository personRepository,
                          SubmitManager submitManager,
                          ReviewManager reviewManager
    ) {
        this.personRepository = personRepository;
        this.submitManager = submitManager;
        this.reviewManager = reviewManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        var person = personRepository.findById(message.getChatId()).orElseThrow();
        switch (person.getAction()) {
            case SENDING_DATA -> {
                return submitManager.answerMessage(message, bot);
            }
            case SENDING_REVIEW -> {
                return reviewManager.answerMessage(message, bot);
            }
        }
        return null;
    }
}
