package com.bots.telegrambotnutritionist.bot.service.manager.start;

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
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    @Autowired
    public StartManager(AnswerMethodFactory methodFactory,
                        KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        try{
            bot.execute(methodFactory.getSendPhoto(
                    chatId,
                    "\\static\\pictures\\Example.jpg"
//                    """
//                            Привет, я Екатерина Шевченко.
//                            В этом боте вы узнаете обо мне, моих курсах и отзывах.
//                             """,
//                    keyboardFactory.getInlineKeyboard(
//                            List.of("Обо мне", "Сопровождение", "Вебинары", "\uD83D\uDCE1 Отзывы"),
//                            List.of(2, 2),
//                            List.of(ABOUT, SUPPORT, WEBINARS, REVIEWS)
//                    )
            ));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return methodFactory.getSendMessage(
                chatId,
                """
                           Привет, я Екатерина Шевченко.
                            В этом боте вы узнаете обо мне, моих курсах и отзывах.
                             """,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Обо мне", "Сопровождение", "Вебинары", "\uD83D\uDCE1 Отзывы"),
                            List.of(2, 2),
                            List.of(ABOUT, SUPPORT, WEBINARS, REVIEWS)
                    )
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        Long chatId = callbackQuery.getMessage().getChatId();
        SendPhoto sendPhoto = methodFactory.getSendPhoto(
                chatId,
                "\\static\\pictures\\Example.jpg"
//                """
//                        Привет, я Екатерина Шевченко.
//                        В этом боте вы узнаете обо мне, моих курсах и отзывах.
//                         """,
//                keyboardFactory.getInlineKeyboard(
//                        List.of("Обо мне", "Сопровождение", "Вебинары", "\uD83D\uDCE1 Отзывы"),
//                        List.of(2, 2),
//                        List.of(ABOUT, SUPPORT, WEBINARS, REVIEWS)
//                )
        );
        try {
            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
//        try{
//            bot.execute(methodFactory.getSendPhoto(
//                    callbackQuery.getMessage().getChatId(),
//                    "\\static\\pictures\\Example.jpg",
//                    """
//                            Привет, я Екатерина Шевченко.
//                            В этом боте вы узнаете обо мне, моих курсах и отзывах.
//                             """,
//                    keyboardFactory.getInlineKeyboard(
//                            List.of("Обо мне", "Сопровождение", "Вебинары", "\uD83D\uDCE1 Отзывы"),
//                            List.of(2, 2),
//                            List.of(ABOUT, SUPPORT, WEBINARS, REVIEWS)
//                    )
//            ));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
        return methodFactory.getEditMessageText(
                callbackQuery,
                """
                           Привет, я Екатерина Шевченко.
                            В этом боте вы узнаете обо мне, моих курсах и отзывах.
                            """,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Обо мне", "Сопровождение", "Вебинары", "\uD83D\uDCE1 Отзывы"),
                            List.of(2, 2),
                            List.of(ABOUT, SUPPORT, WEBINARS, REVIEWS)
                    )
        );
    }
}