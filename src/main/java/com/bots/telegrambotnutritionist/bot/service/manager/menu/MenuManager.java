package com.bots.telegrambotnutritionist.bot.service.manager.menu;

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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @SneakyThrows
    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(message.getChatId());
        InputFile inputFile = new InputFile("D:\\Projects\\TelegramBotNutritionist\\src\\main\\resources\\static\\pictures\\Катя.jpg");
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setCaption("""
            Привет, я Екатерина Шевченко.
            В этом боте вы узнаете обо мне, моих курсах и отзывах.
            """);
        sendPhoto.setReplyMarkup(keyboardFactory.getInlineKeyboard(
                List.of("Обо мне", "Сопровождение", "Вебинары", "\uD83D\uDCE1 Отзывы"),
                List.of(2, 1, 1),
                List.of(ABOUT, SUPPORT, WEBINARS, REVIEWS)
        ));
        sendPhoto.setParseMode("HTML");
        bot.execute(sendPhoto);
//        log.error("Error while sending photo with message and keyboard", e);
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        Message message = null;
        try {
            message = bot.execute(methodFactory.getSendPhoto(
                    callbackQuery.getMessage().getChatId(),
                    "\\static\\pictures\\Катя.jpg",
                    """
                            Привет, я Екатерина Шевченко.
                            В этом боте вы узнаете обо мне, моих курсах и отзывах.
                             """,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Обо мне", "Сопровождение", "Вебинары", "\uD83D\uDCE1 Отзывы"),
                            List.of(2, 2),
                            List.of(ABOUT, SUPPORT, WEBINARS, REVIEWS)
                    )
            ));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
