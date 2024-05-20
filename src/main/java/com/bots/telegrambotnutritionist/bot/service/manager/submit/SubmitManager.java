package com.bots.telegrambotnutritionist.bot.service.manager.submit;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.factory.KeyboardFactory;
import com.bots.telegrambotnutritionist.bot.service.manager.AbstractManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.bots.telegrambotnutritionist.bot.enity.person.Action.SENDING_DATA;
import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmitManager extends AbstractManager {
    final PersonRepository personRepository;
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    @Autowired
    public SubmitManager(PersonRepository personRepository,
                         AnswerMethodFactory methodFactory,
                         KeyboardFactory keyboardFactory) {
        this.personRepository = personRepository;
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        Person person = personRepository.findById(chatId).orElseThrow();
        if (person.getName() == null){
            person.setAction(SENDING_DATA);
            personRepository.save(person);
        }
        return methodFactory.getSendMessage(
                chatId,
                "Ваше имя?",
                keyboardFactory.getInlineKeyboard(
                        List.of("Отмена операции"),
                        List.of(1),
                        List.of(SUBMIT_CANCEL)
                )
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        try {
            bot.execute(methodFactory.getDeleteMessage( // удаляем предыдущее сообщение
                    chatId,
                    message.getMessageId() - 1
            ));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        var person = personRepository.findPersonById(chatId);
        switch (person.getAction()) {
            case SENDING_DATA -> {
                return getData(message, person);
            }
        }

        return null;
    }
    private BotApiMethod<?> getData(Message message, Person person) {
        String text = message.getText();
        if (!message.hasText()) {
            return methodFactory.getSendMessage(
                    message.getChatId(),
                    """
                            Сообщение должно содержать текст
                             """,
                    keyboardFactory.getInlineKeyboard(
                            List.of("❌ Отмена операции"),
                            List.of(1),
                            List.of(SUBMIT_CANCEL)
                    )
            );
        }
        person.setName(text);
        personRepository.save(person);
        return null;
    }


    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        if (callbackData.equals(SUBMIT)){
            Person person = personRepository.findById(callbackQuery.getMessage().getChatId())
                    .orElseThrow();
            if (person.getName() == null){
                person.setAction(SENDING_DATA);
                personRepository.save(person);
            }
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "Ваше имя?",
                    keyboardFactory.getInlineKeyboard(
                            List.of("Отмена операции"),
                            List.of(1),
                            List.of(SUBMIT_CANCEL)
                    )
            );
        }
        String[] callback = callbackData.split("_");
        if (callback.length > 1){
            switch (callbackData) {
                case SUBMIT_CANCEL -> {
                    try {
                        return cancel(callbackQuery, bot);
                    } catch (TelegramApiException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    private BotApiMethod<?> cancel(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        Long chatId = callbackQuery.getMessage().getChatId();
        var person = personRepository.findPersonById(chatId);
        person.setAction(Action.FREE);
        personRepository.save(person);
        bot.execute(methodFactory.getAnswerCallbackQuery(
                callbackQuery.getId(),
                "✅ Операция отменена успешно"
        ));

        return methodFactory.getDeleteMessage(
                chatId,
                callbackQuery.getMessage().getMessageId()
        );
    }
}
