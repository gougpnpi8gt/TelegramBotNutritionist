package com.bots.telegrambotnutritionist.bot.service.manager.auth;

import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.manager.AbstractManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import com.bots.telegrambotnutritionist.bot.util.DescriptionCommands;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final DescriptionCommands commands;

    @Autowired
    public AuthManager(AnswerMethodFactory methodFactory,
                       DescriptionCommands commands) {
        this.methodFactory = methodFactory;
        this.commands = commands;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        Map<String, String> map = commands.builtFirstCommand();
        try {
            bot.execute(methodFactory.getBotCommandScopeChat(
                    chatId,
                    map)
            );
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return null;

    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return null;
    }
}