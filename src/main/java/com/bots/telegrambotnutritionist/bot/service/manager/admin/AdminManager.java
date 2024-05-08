package com.bots.telegrambotnutritionist.bot.service.manager.admin;

import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.manager.AbstractManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import com.bots.telegrambotnutritionist.bot.util.DescriptionCommands;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final DescriptionCommands descriptionCommands;

    @Autowired
    public AdminManager(AnswerMethodFactory methodFactory,
                        DescriptionCommands descriptionCommands) {
        this.methodFactory = methodFactory;
        this.descriptionCommands = descriptionCommands;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        var map = descriptionCommands.adminCommands();
        try {
            bot.execute(methodFactory.getBotCommandScopeChat(
                    chatId,
                    map)
            );
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return methodFactory.getSendMessage(
                chatId,
                """
                        Привет! Чем могу служить?)
                        Дополнительные команды:
                        1. /listPersons - список пользователей
                        2. /listSupports - список заявок на сопровождение
                        3. /listWebinars - список вебинаров
                        4. /listPricesWebinars - список цен вебинаров
                        5. /sales - график продаж
                        6. /editor - редактирование команд бота
                        """,
                null
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return methodFactory.getEditMessageText(
                callbackQuery,
                """
                        Привет! Чем могу служить?)
                        Дополнительные команды:
                        1. /listPersons - выводит список пользователей
                        2. /listSupports - выводит список заявок на сопровождение
                        3. /listWebinars - выводит список вебинаров
                        4. /listPricesWebinars - выводит список цен вебинаров
                        5. /sales - график продаж
                        6. /editor - Редактирование команд
                        """,
                null
        );
    }
}
