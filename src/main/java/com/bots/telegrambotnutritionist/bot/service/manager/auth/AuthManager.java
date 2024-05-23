package com.bots.telegrambotnutritionist.bot.service.manager.auth;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import com.bots.telegrambotnutritionist.bot.enity.person.Role;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.factory.KeyboardFactory;
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

import java.util.List;
import java.util.Map;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;


@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final DescriptionCommands commands;
    final PersonRepository personRepository;
    final KeyboardFactory keyboardFactory;

    @Autowired
    public AuthManager(AnswerMethodFactory methodFactory,
                       DescriptionCommands commands,
                       PersonRepository personRepository, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.commands = commands;
        this.personRepository = personRepository;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        var person = personRepository.findById(chatId).orElseThrow();
        person.setAction(Action.AUTH);
        person.setRole(Role.CLIENT);
        String profile = getFirstProfile(person);
        personRepository.save(person);
        return methodFactory.getSendMessage(chatId,
                profile,
                keyboardFactory.getInlineKeyboard(
                        List.of("Нажмите кнопку, чтобы добавить меню"),
                        List.of( 1),
                        List.of(AUTH_CLIENT)
                )
        );
    }
    private String getFirstProfile(Person person){
        StringBuilder builder = new StringBuilder();
        builder.append("Вам назначен chatId: ").append(person.getId()).append("\n")
                .append("Токен: ").append(person.getToken());
        return builder.toString();
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        var person = personRepository.findById(chatId).orElseThrow();
        person.setRole(Role.CLIENT);
        Map<String, String> map = commands.builtFirstCommand();
        try {
            bot.execute(methodFactory.getBotCommandScopeChat(
                    chatId,
                    map)
            );
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        person.setAction(Action.FREE);
        personRepository.save(person);
        try {
            bot.execute(methodFactory.getAnswerCallbackQuery(callbackQuery.getId(),
                    "Команды добавлены, можете ознакомиться"));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return methodFactory.getDeleteMessage(chatId, messageId);
    }
}