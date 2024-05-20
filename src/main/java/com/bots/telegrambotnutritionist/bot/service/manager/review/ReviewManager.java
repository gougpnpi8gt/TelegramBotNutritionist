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

import java.util.List;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ReviewManager extends AbstractManager {
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
                    """
                            Сообщение должно содержать текст, повторите попытку
                         """,
                   null
            );
        }
        Reviews review = new Reviews(person, message.getText());
        reviewRepository.save(review);
        person.setAction(Action.FREE);
        personRepository.save(person);
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        switch (callbackData) {
            case REVIEWS_PREV -> {
                return null;
            }
            case REVIEWS_NEXT -> {
                return null;
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

    private BotApiMethod<?> mainMenu(Message message){
        List<Reviews> list = reviewRepository.findAll();
        Long chatId = message.getChatId();
        if (list.isEmpty()){
            return methodFactory.getSendMessage(
                    chatId,
                    "Отзывов пока нет",
                    keyboardFactory.getInlineKeyboard(
                            List.of("Оставить отзыв", "Вернитесь в меню"),
                            List.of(1, 1),
                            List.of(REVIEWS_ADD, MENU)
                    )
            );
        }
        return methodFactory.getSendMessage(
                chatId,
                "Всего отзывов: " + list.size() + "\n" +
                       list.get(0).getDescription(),
                keyboardFactory.getInlineKeyboard(
                        List.of("◀\\uFE0F", "▶\\uFE0F", "Оставить отзыв", "Вернитесь в меню"),
                        List.of(2, 1, 1),
                        List.of(REVIEWS_PREV, REVIEWS_NEXT, REVIEWS_ADD, MENU)
                )
        );
    }

    private BotApiMethod<?> mainMenu(CallbackQuery callbackQuery){
        List<Reviews> list = reviewRepository.findAll();
        if (list.isEmpty()){
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "Отзывов пока нет",
                    keyboardFactory.getInlineKeyboard(
                            List.of("Оставить отзыв", "Вернитесь в меню"),
                            List.of(1, 1),
                            List.of(REVIEWS_ADD, MENU)
                    )
            );
        }
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Всего отзывов: " + list.size() + "\n" +
                        list.get(0).getDescription(),
                keyboardFactory.getInlineKeyboard(
                        List.of("◀\\uFE0F", "▶\\uFE0F", "Оставить отзыв", "Вернитесь в меню"),
                        List.of(2, 1, 1),
                        List.of(REVIEWS_PREV, REVIEWS_NEXT, REVIEWS_ADD, MENU)
                )
        );
    }

//    private BotApiMethod<?> prevReview(CallbackQuery callbackQuery, Bot bot, List<Reviews> reviews) {
//        try {
//            bot.execute(methodFactory.getEditMessagePhoto(
//                    callbackQuery,
//                    iterator(reviews),
//                    keyboardFactory.getInlineKeyboard(
//                            List.of("◀\uFE0F", "▶\uFE0F", "В меню"),
//                            List.of(2, 1),
//                            List.of(REVIEWS_PREV, REVIEWS_NEXT, MENU)
//                    )
//            ));
//        } catch (TelegramApiException e) {
//            log.error(e.getMessage());
//        }
//        return null;
//    }

//    private BotApiMethod<?> nextReview(CallbackQuery callbackQuery, Bot bot, List<Reviews> reviews) {
//        try {
//            bot.execute(methodFactory.getEditMessagePhoto(
//                    callbackQuery,
//                    iterator(reviews),
//                    keyboardFactory.getInlineKeyboard(
//                            List.of("◀\uFE0F", "▶\uFE0F", "В меню"),
//                            List.of(2, 1),
//                            List.of(REVIEWS_PREV, REVIEWS_NEXT, MENU)
//                    )
//            ));
//        } catch (TelegramApiException e) {
//            log.error(e.getMessage());
//        }
//        return null;
//    }
}