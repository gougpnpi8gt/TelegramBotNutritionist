package com.bots.telegrambotnutritionist.bot.service.manager.review;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import com.bots.telegrambotnutritionist.bot.enity.reviews.Reviews;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.repository.ReviewRepository;
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
public class ReviewManager extends AbstractManager {
    static int count = 0;
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    final ReviewRepository reviewRepository;
    final PersonRepository personRepository;

    @Autowired
    public ReviewManager(AnswerMethodFactory methodFactory,
                         KeyboardFactory keyboardFactory,
                         ReviewRepository reviewRepository,
                         PersonRepository personRepository) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.reviewRepository = reviewRepository;
        this.personRepository = personRepository;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return mainMenu(message);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        Person person = personRepository.findPersonById(chatId);
        if (!message.hasText()) {
            return methodFactory.getSendMessage(
                    message.getChatId(),
                    "Сообщение должно содержать текст, повторите попытку",
                    null
            );
        }
        Reviews review = new Reviews(person, message.getText());
        if (person.getReviews().size() < 3) {
            person.addReviews(review);
            reviewRepository.save(review);
        } else {
            try {
                bot.execute(methodFactory.getSendMessage(
                        chatId,
                        "У вас уже больше 3 оставленных отзывов",
                        null)
                );
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
        person.setAction(Action.FREE);
        personRepository.save(person);
        return mainMenu(message);
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        switch (callbackData) {
            case REVIEWS_PREV -> {
                return prevReview(callbackQuery);
            }
            case REVIEWS_NEXT -> {
                return nextReview(callbackQuery);
            }
            case REVIEWS_ADD -> {
                return addReviews(callbackQuery);
            }
            case REVIEWS_MENU -> {
                return mainMenu(callbackQuery);
            }
        }
        return mainMenu(callbackQuery);
    }

    private BotApiMethod<?> addReviews(CallbackQuery callbackQuery) {
        Person person = personRepository.findPersonById(callbackQuery.getMessage().getChatId());
        person.setAction(Action.SENDING_REVIEW);
        personRepository.save(person);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Напишите ваш отзыв",
                keyboardFactory.getInlineKeyboard(
                        List.of("Отмена"),
                        List.of(1),
                        List.of(REVIEWS_MENU)
                )
        );
    }

    private BotApiMethod<?> mainMenu(Message message) {
        List<Reviews> reviews = reviewRepository.findAll();
        Long chatId = message.getChatId();
        if (reviews.isEmpty()) {
            return methodFactory.getSendMessage(
                    chatId,
                    "Отзывов пока нет",
                    getEmptyKeyboard()
            );
        }
        return methodFactory.getSendMessage(
                chatId,
                getDescriptionReview(reviews, REVIEWS_MENU),
                getStandardKeyboard()
        );
    }

    private BotApiMethod<?> mainMenu(CallbackQuery callbackQuery) {
        var person = personRepository.findPersonById(callbackQuery.getMessage().getChatId());
        if (person.getAction() == Action.SENDING_REVIEW) {
            person.setAction(Action.FREE);
            personRepository.save(person);
        }
        List<Reviews> reviews = reviewRepository.findAll();
        if (reviews.isEmpty()) {
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "Отзывов пока нет",
                    getEmptyKeyboard()
            );
        }
        return methodFactory.getEditMessageText(
                callbackQuery,
                getDescriptionReview(reviews, callbackQuery.getData()),
                getStandardKeyboard()
        );
    }

    private BotApiMethod<?> prevReview(CallbackQuery callbackQuery) {
        List<Reviews> reviews = reviewRepository.findAll();
        return methodFactory.getEditMessageText(
                callbackQuery,
                getDescriptionReview(reviews, callbackQuery.getData()),
                getStandardKeyboard()
        );
    }

    private BotApiMethod<?> nextReview(CallbackQuery callbackQuery) {
        List<Reviews> reviews = reviewRepository.findAll();
        return methodFactory.getEditMessageText(
                callbackQuery,
                getDescriptionReview(reviews, callbackQuery.getData()),
                getStandardKeyboard()
        );
    }

    private InlineKeyboardMarkup getEmptyKeyboard() {
        return keyboardFactory.getInlineKeyboard(
                List.of("Оставить отзыв", "Вернитесь в меню"),
                List.of(1, 1),
                List.of(REVIEWS_ADD, MENU)
        );
    }

    private InlineKeyboardMarkup getStandardKeyboard() {
        return keyboardFactory.getInlineKeyboard(
                List.of("◀", "▶", "Оставить отзыв", "Вернитесь в меню"),
                List.of(2, 1, 1),
                List.of(REVIEWS_PREV, REVIEWS_NEXT, REVIEWS_ADD, MENU)
        );
    }

    private String getDescriptionReview(List<Reviews> reviews, String data) {
        int size = reviews.size();
        count = (data.equals(REVIEWS_PREV)) ? ((count == 0) ? (size - 1) : count - 1)
                : (data.equals(REVIEWS_NEXT)) ? ((count == (size - 1)) ? 0 : count + 1)
                : 0;
        StringBuilder builder = new StringBuilder();
        builder.append("Всего отзывов: ").append(size).append("\n")
                .append(reviews.get(count).getDescription());
        return builder.toString();
    }
}