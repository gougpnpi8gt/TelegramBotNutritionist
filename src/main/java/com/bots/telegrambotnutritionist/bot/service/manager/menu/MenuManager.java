package com.bots.telegrambotnutritionist.bot.service.manager.menu;

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
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuManager extends AbstractManager {

    final KeyboardFactory keyboardFactory;
    final AnswerMethodFactory methodFactory;
    @Autowired
    public MenuManager(KeyboardFactory keyboardFactory, AnswerMethodFactory methodFactory) {
        this.keyboardFactory = keyboardFactory;
        this.methodFactory = methodFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot){
        try {
            bot.execute(methodFactory.getSendPhoto(
                    message.getChatId(),
                    "\\static\\pictures\\Катя.jpg",
                    """
                            Привет, я Екатерина Шевченко.
                            В этом боте вы узнаете обо мне, моих курсах и отзывах.
                             """
//                    keyboardFactory.getInlineKeyboard(
//                            List.of("Обо мне", "Сопровождение", "Вебинары", "\uD83D\uDCE1 Отзывы"),
//                            List.of(2, 2),
//                            List.of(ABOUT, SUPPORT, WEBINARS, REVIEWS)
//                    )
            ));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        try {
            bot.execute(methodFactory.getSendPhoto(
                    callbackQuery.getMessage().getChatId(),
                    "\\static\\pictures\\Катя.jpg",
                    """
                            Привет, я Екатерина Шевченко.
                            В этом боте вы узнаете обо мне, моих курсах и отзывах.
                             """
//                    keyboardFactory.getInlineKeyboard(
//                            List.of("Обо мне", "Сопровождение", "Вебинары", "\uD83D\uDCE1 Отзывы"),
//                            List.of(2, 2),
//                            List.of(ABOUT, SUPPORT, WEBINARS, REVIEWS)
//                    )
            ));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
