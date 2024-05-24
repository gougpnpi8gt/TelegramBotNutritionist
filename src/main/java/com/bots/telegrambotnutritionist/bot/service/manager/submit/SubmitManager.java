package com.bots.telegrambotnutritionist.bot.service.manager.submit;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import com.bots.telegrambotnutritionist.bot.enity.support.Support;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.repository.SupportRepository;
import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.factory.KeyboardFactory;
import com.bots.telegrambotnutritionist.bot.service.manager.AbstractManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
    final SupportRepository supportRepository;

    @Autowired
    public SubmitManager(PersonRepository personRepository,
                         AnswerMethodFactory methodFactory,
                         KeyboardFactory keyboardFactory,
                         SupportRepository supportRepository) {
        this.personRepository = personRepository;
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.supportRepository = supportRepository;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return mainMenu(message);
    }

    private BotApiMethod<?> mainMenu(Message message) {
        return methodFactory.getSendMessage(message.getChatId(),
                "Вы уже оставляли заявку?",
                keyboardFactory.getInlineKeyboard(
                        List.of("Да", "Нет"),
                        List.of(1, 1),
                        List.of(SUBMIT_YES, SUBMIT_NO)
                )
        );
    }


    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
//        String text = message.getText();
//        var person = personRepository.findPersonById(message.getChatId());
//        person.setName(text);
//        personRepository.save(person);
//
//
        Support support = new Support();
        return null;
    }
    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callback = callbackQuery.getData();
        switch (callback) {
            case SUBMIT_YES -> {
                return yesSubmit(callbackQuery);
            }
            case SUBMIT_NO -> {
                return noSubmit(callbackQuery);
            }
            case SUBMIT_CANCEL -> {
                return cancel(callbackQuery, bot);
            }
            default -> {
                return mainMenu(callbackQuery);
            }
        }
    }

    private BotApiMethod<?> cancel(CallbackQuery callbackQuery, Bot bot) {
        var person = personRepository.findPersonById(callbackQuery.getMessage().getChatId());
        person.setAction(Action.FREE);
        personRepository.save(person);
        try {
            bot.execute(methodFactory.getAnswerCallbackQuery(callbackQuery.getId(), "Операция отменена"));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return mainMenu(callbackQuery);
    }

    private BotApiMethod<?> mainMenu(CallbackQuery callbackQuery) {
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Вы уже оставляли заявку?",
                keyboardFactory.getInlineKeyboard(
                        List.of("Да", "Нет"),
                        List.of(1, 1),
                        List.of(SUBMIT_YES, SUBMIT_NO)
                )
        );
    }

    private BotApiMethod<?> noSubmit(CallbackQuery callbackQuery) {
        var person = personRepository.findPersonById(callbackQuery.getMessage().getChatId());
        person.setAction(SENDING_DATA);
        personRepository.save(person);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Введите свое имя",
                null
        );
    }

    private BotApiMethod<?> yesSubmit(CallbackQuery callbackQuery) {
        var person = personRepository.findPersonById(callbackQuery.getMessage().getChatId());
        Support support = supportRepository.findByPerson(person);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Заявка была создана - " + support.getTimeOfCreation().toString(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Вернуться в меню"),
                        List.of(1),
                        List.of(MENU)
                )
        );
    }
}