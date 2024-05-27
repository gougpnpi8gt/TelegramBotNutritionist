package com.bots.telegrambotnutritionist.bot.service.handler;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.service.manager.answer.AnswerManager;
import com.bots.telegrambotnutritionist.bot.service.manager.auth.AuthManager;
import com.bots.telegrambotnutritionist.bot.service.manager.editor.EditorManager;
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
    final EditorManager editorManager;
    final AuthManager authManager;
    final AnswerManager answerManager;

    @Autowired
    public MessageHandler(PersonRepository personRepository,
                          SubmitManager submitManager,
                          ReviewManager reviewManager,
                          EditorManager editorManager,
                          AuthManager authManager,
                          AnswerManager answerManager
    ) {
        this.personRepository = personRepository;
        this.submitManager = submitManager;
        this.reviewManager = reviewManager;
        this.editorManager = editorManager;
        this.authManager = authManager;
        this.answerManager = answerManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        var person = personRepository.findById(message.getChatId()).orElseThrow();
        Action action = person.getAction();
        String[] data = action.toString().split("_");
        if (data.length > 1){
            return submitManager.answerMessage(message, bot);
        }
        switch (action) {
            case AUTH -> {
                return authManager.answerMessage(message, bot);
            }
            case SENDING_REVIEW -> {
                return reviewManager.answerMessage(message, bot);
            }
            case ADD_COMMAND, DELETE_COMMAND, UPDATE_COMMAND -> {
                return editorManager.answerMessage(message, bot);
            }
            case SENDING_QUESTION, SENDING_ADMIN -> {
                return answerManager.answerMessage(message, bot);
            }
        }
        return null;
    }
}
