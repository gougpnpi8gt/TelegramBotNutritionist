package com.bots.telegrambotnutritionist.bot.service.manager.submit;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.enity.person.ContactInformation;
import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import com.bots.telegrambotnutritionist.bot.enity.support.Support;
import com.bots.telegrambotnutritionist.bot.repository.ContactsRepository;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.repository.SupportRepository;
import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.factory.KeyboardFactory;
import com.bots.telegrambotnutritionist.bot.service.manager.AbstractManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
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
import java.util.Set;
import java.util.stream.Collectors;

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
    final ContactsRepository contactsRepository;

    @Autowired
    public SubmitManager(PersonRepository personRepository,
                         AnswerMethodFactory methodFactory,
                         KeyboardFactory keyboardFactory,
                         SupportRepository supportRepository,
                         ContactsRepository contactsRepository) {
        this.personRepository = personRepository;
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.supportRepository = supportRepository;
        this.contactsRepository = contactsRepository;
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

    private void protectionData(Person person, ContactInformation contact, Long chatId,
                                String caption, Bot bot) throws TelegramApiException {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        if (person != null && contact == null){
            Set<ConstraintViolation<Person>> violations = validator.validate(person);
            if (violations.isEmpty()) {
                personRepository.save(person);
                if (caption != null){
                    bot.execute(methodFactory.getSendMessage(chatId, caption, null));
                }
            } else {
                StringBuilder errorMessage = new StringBuilder("Ошибка: ");
                for (ConstraintViolation<Person> violation : violations) {
                    errorMessage.append(violation.getMessage()).append(" ");
                }
                bot.execute(methodFactory.getSendMessage(chatId, errorMessage.toString(), null));
            }
        } else if (contact != null && person != null){
            Set<ConstraintViolation<ContactInformation>> violations1 = validator.validate(contact);
            Set<ConstraintViolation<Person>> violations2 = validator.validate(person);
            if (violations1.isEmpty() && violations2.isEmpty()) {
                personRepository.save(person);
                contactsRepository.save(contact);
                if (caption != null){
                    bot.execute(methodFactory.getSendMessage(chatId, caption, null));
                }
            } else {
                StringBuilder errorMessage = new StringBuilder("Ошибка: ");
                for (ConstraintViolation<ContactInformation> violation : violations1) {
                    errorMessage.append(violation.getMessage()).append(" ");
                }
                for (ConstraintViolation<Person> violation : violations2) {
                    errorMessage.append(violation.getMessage()).append(" ");
                }
                bot.execute(methodFactory.getSendMessage(chatId, errorMessage.toString(), null));
            }
        }
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
                person.addSupport(support);
                person.setAction(FREE);
                personRepository.save(person);
                supportRepository.save(support);
                bot.execute(methodFactory.getSendMessage(
                        chatId,
                        "Заявка успешно отправлена",
                        null));

                return mainMenu(message);
            }
            case DATE_NAME -> {
                person.setName(text);
                person.setAction(DATE_SURNAME);
                protectionData(person, null, chatId, "Введите фамилию: ", bot);
            }
            case DATE_SURNAME -> {
                person.setSurName(text);
                person.setAction(DATE_PATRONYMIC);
                protectionData(person, null, chatId, "Введите отчество. Если его нет, введите \"нет\". ", bot);
            }
            case DATE_PATRONYMIC -> {
                person.setPatronymic(text);
                person.setAction(DATE_AGE);
                protectionData(person, null, chatId, "\uD83D\uDD6F Введите возраст: ", bot);
            }
            case DATE_AGE -> {
                person.setAge(Integer.parseInt(text));
                person.setAction(DATE_GENDER);
                protectionData(person, null, chatId, "Выберите гендер: М\uD83D\uDC66\uD83C\uDFFB или" +
                        " \uD83D\uDC69\uD83C\uDFFB\u200D\uD83E\uDDB0Ж ", bot);
            }
            case DATE_GENDER -> {
                if (text.equalsIgnoreCase("м")){
                    person.setGender(MAN);
                } else {
                    person.setGender(WOMAN);
                }
                person.setAction(DATE_WEIGHT);
                protectionData(person, null, chatId, "⚖\uFE0F Введите ваш вес: ", bot);
            }
            case DATE_WEIGHT -> {
                person.setWeight(Integer.parseInt(text));
                person.setAction(DATE_BIRTHDAY);
                protectionData(person, null, chatId,"\uD83D\uDCC6 Введите дату рождения в формате 31-12-1975", bot);
            }
            case DATE_BIRTHDAY -> {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate localDate = LocalDate.parse(text, dtf);
                person.setBirthday(localDate);
                person.setAction(DATE_COUNTRY);
                protectionData(person, null, chatId, "\uD83C\uDDF7\uD83C\uDDFA Введите страну", bot);
            }
            case DATE_COUNTRY -> {
                person.setCountry(text);
                person.setAction(DATE_CHARACTERISTIC);
                protectionData(person, null, chatId, "\uD83D\uDCC4 Характеристика", bot);
            }
            case DATE_CHARACTERISTIC -> {
                person.setCharacteristicsOfAPerson(text);
                person.setAction(DATE_EMAIL);
                protectionData(person, null, chatId, "\uD83D\uDCED Введите емаил", bot);
            }
            case DATE_EMAIL -> {
                ContactInformation contact = contactsRepository.findContactInformationByPerson(person);
                if (contact == null){
                    contact = new ContactInformation();
                }
                contact.setEmail(text);
                contact.setPerson(person);
                person.setAction(DATE_PHONE);
                protectionData(person, contact, chatId,
                        "\uD83D\uDCDE Отправьте номер телефона в формате: +7(917)-111-22-33", bot);
            }
            case DATE_PHONE -> {
                ContactInformation contact = contactsRepository.findContactInformationByPerson(person);
                contact.setPhone(text);
                person.setAction(DATE_SUPPORT);
                protectionData(person, contact, chatId, "Заявка заполнена! Напишите \"Да\"", bot);
            }
            case DATE_SUPPORT -> {
                person.setAction(FREE);
                personRepository.save(person);
                return methodFactory.getSendMessage(
                        chatId,
                        "Отправить заявку?",
                        keyboardFactory.getInlineKeyboard(
                                List.of("✅", "❌", "\uD83D\uDD0D Проверить свой профиль"),
                                List.of(2, 1),
                                List.of(SUBMIT_SEND, SUBMIT_CANCEL, SUBMIT_CHECK)
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
            case SUBMIT_NEW -> {
                return newSubmit(callbackQuery);
            }
            case SUBMIT_CHECK -> {
                return checkSubmit(callbackQuery);
            }
            default -> {
                return mainMenu(callbackQuery);
            }
        }
    }

    private BotApiMethod<?> checkSubmit(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Person person = findPerson(chatId);
        ContactInformation contact = contactsRepository.findContactInformationByPerson(person);
        StringBuilder builder = new StringBuilder();
        builder
                .append("Данные пользователя: ").append("\n")
                .append("\uD83D\uDCCC Имя - ").append(person.getName()).append(" \n")
                .append("\uD83D\uDCCC Фамилия - ").append(person.getSurName()).append(" \n")
                .append("\uD83D\uDCCC Отчетсво - ").append(person.getPatronymic()).append(" \n")
                .append("\uD83D\uDCCC Возраст - ").append(person.getAge()).append(" \n")
                .append("\uD83D\uDCCC Пол - ").append(person.getGender()).append(" \n")
                .append("\uD83D\uDCCC Вес - ").append(person.getWeight()).append(" \n")
                .append("\uD83D\uDCCC День рождения - ").append(person.getBirthday()).append(" \n")
                .append("\uD83D\uDCCC Страна проживания - ").append(person.getCountry()).append(" \n")
                .append("\uD83D\uDCCC Характеристика - ").append(person.getCharacteristicsOfAPerson()).append(" \n")
                .append("Контактная информация пользователя: ").append(" \n")
                .append("\uD83D\uDCCC Емаил - ").append(contact.getEmail()).append(" \n")
                .append("\uD83D\uDCCC Телефон - ").append(contact.getPhone()).append(" \n")
                .append("Отправляем заявку? ");
        person.setAction(SUBMIT_DESCRIPTION);
        personRepository.save(person);
        return methodFactory.getEditMessageText(
                callbackQuery,
                builder.toString(),
                keyboardFactory.getInlineKeyboard(
                        List.of("Да", "Нет"),
                        List.of(1, 1),
                        List.of(SUBMIT_SEND, SUBMIT_CANCEL)
                )
        );
    }

    private BotApiMethod<?> newSubmit(CallbackQuery callbackQuery) {
        Person person = findPerson(callbackQuery.getMessage().getChatId());
        person.setAction(DATE_NAME);
        personRepository.save(person);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Введите свое имя",
                null
        );
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
        if (person.getName() != null && person.getCharacteristicsOfAPerson() != null){
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "Ваше данные уже есть в базе данных, заполнить заявку заново или использовать существующее данные?",
                    keyboardFactory.getInlineKeyboard(
                            List.of("Отправить заявку и добавить новое описание к ней",
                                    "Заполнить новую заявку и изменить данные пользователя",
                                    "В меню сопровождения"),
                            List.of(1, 1, 1),
                            List.of(SUBMIT_SEND, SUBMIT_NEW,
                                    SUPPORT)
                    )
            );
        } else {
            person.setAction(DATE_NAME);
            personRepository.save(person);
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "Введите свое имя",
                    null
            );
        }
    }

    private BotApiMethod<?> yesSubmit(CallbackQuery callbackQuery) {
        Person person = findPerson(callbackQuery.getMessage().getChatId());
        List<Support> supports = supportRepository.findAllByPerson(person);
        if (supports != null){
            StringBuilder builder = new StringBuilder();
            builder.append("Количество  оставленных вами заявок: ").append(supports.size()).append(" \n")
                    .append(supports.stream().map(Support::toString).collect(Collectors.toList()));
            String str = String.valueOf(builder).replace("[", "").replace("]", "");
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    str,
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