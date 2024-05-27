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
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static com.bots.telegrambotnutritionist.bot.enity.person.Action.*;
import static com.bots.telegrambotnutritionist.bot.enity.person.Gender.*;
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


    @SneakyThrows
    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        String text = message.getText();
        var person = findPerson(message.getChatId());
        Long chatId = message.getChatId();
        switch (person.getAction()){
            case SUBMIT_DESCRIPTION -> {
                Support support = Support.builder()
                        .description(text)
                        .timeOfCreation(LocalDate.now())
                        .person(person)
                        .build();
                supportRepository.save(support);
                person.setAction(FREE);
                personRepository.save(person);
                bot.execute(methodFactory.getSendMessage(
                        chatId,
                        "Заявка успешно отправлена",
                        null));

                return mainMenu(message);
            }
            case DATE_NAME -> {
                person.setName(text);
                person.setAction(DATE_SURNAME);
                personRepository.save(person);
                bot.execute(methodFactory.getSendMessage(chatId, "Введите фамилию", null));
            }
            case DATE_SURNAME -> {
                person.setSurName(text);
                person.setAction(DATE_PATRONYMIC);
                personRepository.save(person);
                bot.execute(methodFactory.getSendMessage(chatId, "Введите отчество. Если его нет - введите \"нет\"", null));
            }
            case DATE_PATRONYMIC -> {
                person.setPatronymic(text);
                person.setAction(DATE_AGE);
                personRepository.save(person);
                bot.execute(methodFactory.getSendMessage(chatId, "Введите возраст", null));
            }
            case DATE_AGE -> {
                person.setAge(Integer.parseInt(text));
                person.setAction(DATE_GENDER);
                personRepository.save(person);
                bot.execute(methodFactory.getSendMessage(chatId, "Выберите гендер: М или Ж", null));
            }
            case DATE_GENDER -> {
                if (text.equalsIgnoreCase("м")){
                    person.setGender(MALE);
                } else {
                    person.setGender(WOMAN);
                }
                person.setAction(DATE_WEIGHT);
                personRepository.save(person);
                bot.execute(methodFactory.getSendMessage(chatId, "Введите вес", null));
            }
            case DATE_WEIGHT -> {
                person.setWeight(Integer.parseInt(text));
                person.setAction(DATE_BIRTHDAY);
                personRepository.save(person);
                bot.execute(methodFactory.getSendMessage(chatId, "Введите дату рождения в формате месяц-день-год(03-31-1975)", null));
            }
            case DATE_BIRTHDAY -> {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                LocalDate localDate = LocalDate.parse(text, dtf);
                person.setBirthday(localDate);
                person.setAction(DATE_COUNTRY);
                personRepository.save(person);
                bot.execute(methodFactory.getSendMessage(chatId, "Введите страну", null));
            }
            case DATE_COUNTRY -> {
                person.setCountry(text);
                person.setAction(DATE_CHARACTERISTIC);
                personRepository.save(person);
                bot.execute(methodFactory.getSendMessage(chatId, "Характеристика", null));
            }
            case DATE_CHARACTERISTIC -> {
                person.setCharacteristicsOfAPerson(text);
                person.setAction(FREE);
                personRepository.save(person);
                return methodFactory.getSendMessage(
                        chatId,
                        "Отправить заявку?",
                        keyboardFactory.getInlineKeyboard(
                                List.of("Да", "Нет"),
                                List.of(2),
                                List.of(SUBMIT_SEND, SUBMIT_CANCEL)
                        )
                );
            }
        }
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
            case SUBMIT_SEND -> {
                return send(callbackQuery);
            }
            default -> {
                return mainMenu(callbackQuery);
            }
        }
    }

    private Person findPerson(Long chatId){
        return personRepository.findPersonById(chatId);
    }
    private BotApiMethod<?> send(CallbackQuery callbackQuery) {
        Person person = findPerson(callbackQuery.getMessage().getChatId());
        person.setAction(Action.SUBMIT_DESCRIPTION);
        personRepository.save(person);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Добавьте описание заявки",
                null
        );
    }

    private BotApiMethod<?> cancel(CallbackQuery callbackQuery, Bot bot) {
        var person = findPerson(callbackQuery.getMessage().getChatId());
        person.setAction(Action.FREE);
        personRepository.save(person);
        try {
            bot.execute(methodFactory.getAnswerCallbackQuery(
                    callbackQuery.getId(),
                    "Операция отменена")
            );
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
        Person person = findPerson(callbackQuery.getMessage().getChatId());
        person.setAction(DATE_NAME);
        personRepository.save(person);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Введите свое имя",
                null
        );
    }

    private BotApiMethod<?> yesSubmit(CallbackQuery callbackQuery) {
        Person person = findPerson(callbackQuery.getMessage().getChatId());
        Support support = supportRepository.findByPerson(person);
        if (support != null){
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "Заявка была создана - " + support.getTimeOfCreation().toString(),
                    keyboardFactory.getInlineKeyboard(
                            List.of("Вернуться в меню"),
                            List.of(1),
                            List.of(MENU)
                    )
            );
        } else {
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "Заявка не найдена",
                    keyboardFactory.getInlineKeyboard(
                            List.of("Вернуться в меню"),
                            List.of(1),
                            List.of(MENU)
                    )
            );
        }
    }
}