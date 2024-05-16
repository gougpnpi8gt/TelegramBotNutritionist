package com.bots.telegrambotnutritionist.bot.service.manager.review;

import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.factory.KeyboardFactory;
import com.bots.telegrambotnutritionist.bot.service.manager.AbstractManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewManager extends AbstractManager {

    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    @Autowired
    public ReviewManager(AnswerMethodFactory methodFactory,
                        KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }
    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                null,
                keyboardFactory.getInlineKeyboard(
                        List.of("◀\uFE0F", "▶\uFE0F", "В меню"),
                        List.of(2, 1),
                        List.of(REVIEWS_PREV, REVIEWS_NEXT, MENU)
                )
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String date = callbackQuery.getData();
        String[] strings = date.split("_");
        if (strings.length > 1){
            switch (strings[1]){

            }
        }
        return methodFactory.getEditMessageText(
                callbackQuery,
                null,
                keyboardFactory.getInlineKeyboard(
                        List.of("◀\uFE0F", "▶\uFE0F", "В меню"),
                        List.of(2, 1),
                        List.of(REVIEWS_PREV, REVIEWS_NEXT, MENU)
                )
        );
    }
}
