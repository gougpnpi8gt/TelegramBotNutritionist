package com.bots.telegrambotnutritionist.bot.service.manager.answer;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import com.bots.telegrambotnutritionist.bot.enity.person.Role;
import com.bots.telegrambotnutritionist.bot.enity.question.Question;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.repository.QuestionsRepository;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AnswerManager extends AbstractManager {
    static int count = 0;
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    final PersonRepository personRepository;
    final QuestionsRepository questionsRepository;

    @Autowired
    public AnswerManager(AnswerMethodFactory methodFactory,
                         KeyboardFactory keyboardFactory,
                         PersonRepository personRepository,
                         QuestionsRepository questionsRepository) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.personRepository = personRepository;
        this.questionsRepository = questionsRepository;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return mainMenu(message);
    }

    private BotApiMethod<?> mainMenu(Message message) {
        Long chatId = message.getChatId();
        List<Question> questions = questionsRepository.findAll();
        if (questions.isEmpty()) {
            return methodFactory.getSendMessage(
                    chatId,
                    "Вопросов нет",
                    getEmptyKeyboard()
            );
        }
        Role role = findPerson(chatId).getRole();
        return methodFactory.getSendMessage(
                chatId,
                getDescriptionQuestion(questions, ANSWER),
                getStandardKeyboard(role)
        );
    }

    private InlineKeyboardMarkup getEmptyKeyboard() {
        return keyboardFactory.getInlineKeyboard(
                List.of("Написать вопрос", "Посмотреть отзывы", "Вернуться в меню сопровождения"),
                List.of(2, 1),
                List.of(QUESTION_ADD, REVIEWS, SUPPORT)
        );
    }

    private String getDescriptionQuestion(List<Question> questions, String data) {
        int size = questions.size();
        count = (data.equals(QUESTION_PREV)) ? ((count == 0) ? (size - 1) : count - 1)
                : (data.equals(QUESTION_NEXT)) ? ((count == (size - 1)) ? 0 : count + 1)
                : 0;
        StringBuilder builder = new StringBuilder();
        Question question = questions.get(count);
        if (question.getAnswerAdmin() != null) {
            builder
                    .append("\uD83D\uDCD6").append(" Всего заданных вопросов: ").append(size).append("\n")
                    .append("\uD83D\uDCCC").append(" Описание вопроса: ").append("\n")
                    .append(question.getDescription()).append("\n")
                    .append("✅").append(" Ответ администратора: ").append("\n")
                    .append(question.getAnswerAdmin());
        } else {
            builder.append("Всего заданных вопросов: ").append(size).append("\n")
                    .append("\uD83D\uDCCC").append(" Описание вопроса: ").append("\n")
                    .append(question.getDescription()).append("\n")
                    .append("\uD83C\uDD70\uFE0F").append(" Администратор еще не ответил на вопрос:").append("\n");
        }
        return builder.toString();
    }

    private InlineKeyboardMarkup getStandardKeyboard(Role role) {
        if (role.equals(Role.ADMIN)) {
            return keyboardFactory.getInlineKeyboard(
                    List.of("◀", "▶",
                            "Ответить на вопрос клиента", "Обновить свой ответ на вопрос",
                            "Написать вопрос", "Посмотреть отзывы",
                            "Вернуться в меню сопровождения"),
                    List.of(2, 2, 2),
                    List.of(QUESTION_PREV, QUESTION_NEXT,
                            QUESTION_ADMIN, QUESTION_UPDATE,
                            QUESTION_ADD, REVIEWS,
                            SUPPORT)
            );
        } else {
            return keyboardFactory.getInlineKeyboard(
                    List.of("◀", "▶", "Написать вопрос", "Посмотреть отзывы", "Вернуться в меню сопровождения"),
                    List.of(2, 2, 1),
                    List.of(QUESTION_PREV, QUESTION_NEXT,
                            QUESTION_ADD, REVIEWS,
                            SUPPORT)
            );
        }
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        if (!message.hasText()) {
            return methodFactory.getSendMessage(
                    chatId,
                    "Сообщение должно содержать текст, повторите попытку",
                    null
            );
        }
        Person person = findPerson(chatId);
        String text = message.getText();
        Question question;
        if (person.getAction().equals(Action.SENDING_QUESTION)) {
            question = new Question(text, person);
        } else {
            question = questionsRepository.findAll().get(count);
            question.setAnswerAdmin(text);
        }
        questionsRepository.save(question);
        person.setAction(Action.FREE);
        personRepository.save(person);
        return mainMenu(message);
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        switch (callbackData) {
            case QUESTION_PREV -> {
                return prevQuestion(callbackQuery);
            }
            case QUESTION_NEXT -> {
                return nextQuestion(callbackQuery);
            }
            case QUESTION_ADD -> {
                return addQuestion(callbackQuery);
            }
            case QUESTION_CANCEL -> {
                return cancel(callbackQuery, bot);
            }
            case QUESTION_ADMIN -> {
                return adminAnswer(callbackQuery);
            }
            case QUESTION_UPDATE -> {
                return updateAnswer(callbackQuery);
            }
            default -> {
                return mainMenu(callbackQuery);
            }
        }
    }

    private BotApiMethod<?> updateAnswer(CallbackQuery callbackQuery) {
        Person person = findPerson(callbackQuery.getMessage().getChatId());
        person.setAction(Action.SENDING_ADMIN);
        personRepository.save(person);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Измените ответ на вопрос клиента",
                keyboardFactory.getInlineKeyboard(
                        List.of("Отмена"),
                        List.of(1),
                        List.of(QUESTION_CANCEL)
                )
        );
    }

    private BotApiMethod<?> adminAnswer(CallbackQuery callbackQuery) {
        Person person = findPerson(callbackQuery.getMessage().getChatId());
        person.setAction(Action.SENDING_ADMIN);
        personRepository.save(person);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Напишите ответ на вопрос клиента",
                keyboardFactory.getInlineKeyboard(
                        List.of("Отмена"),
                        List.of(1),
                        List.of(QUESTION_CANCEL)
                )
        );
    }

    private BotApiMethod<?> cancel(CallbackQuery callbackQuery, Bot bot) {
        Person person = findPerson(callbackQuery.getMessage().getChatId());
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

    private BotApiMethod<?> prevQuestion(CallbackQuery callbackQuery) {
        List<Question> questions = questionsRepository.findAll();
        Role role = findPerson(callbackQuery.getMessage().getChatId()).getRole();
        return methodFactory.getEditMessageText(
                callbackQuery,
                getDescriptionQuestion(questions, callbackQuery.getData()),
                getStandardKeyboard(role)
        );
    }

    private BotApiMethod<?> nextQuestion(CallbackQuery callbackQuery) {
        List<Question> questions = questionsRepository.findAll();
        Role role = findPerson(callbackQuery.getMessage().getChatId()).getRole();
        return methodFactory.getEditMessageText(
                callbackQuery,
                getDescriptionQuestion(questions, callbackQuery.getData()),
                getStandardKeyboard(role)
        );
    }

    private BotApiMethod<?> addQuestion(CallbackQuery callbackQuery) {
        Person person = personRepository.findPersonById(callbackQuery.getMessage().getChatId());
        person.setAction(Action.SENDING_QUESTION);
        personRepository.save(person);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Напишите ваш вопрос",
                keyboardFactory.getInlineKeyboard(
                        List.of("Отмена"),
                        List.of(1),
                        List.of(QUESTION_CANCEL)
                )
        );
    }

    private BotApiMethod<?> mainMenu(CallbackQuery callbackQuery) {
        List<Question> questions = questionsRepository.findAll();
        if (questions.isEmpty()) {
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "Вопросов нет",
                    getEmptyKeyboard()
            );
        }
        Role role = findPerson(callbackQuery.getMessage().getChatId()).getRole();
        return methodFactory.getEditMessageText(
                callbackQuery,
                getDescriptionQuestion(questions, ANSWER),
                getStandardKeyboard(role)
        );
    }

    private Person findPerson(Long chatId) {
        return personRepository.findPersonById(chatId);
    }
}