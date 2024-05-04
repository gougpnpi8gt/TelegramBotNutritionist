package com.bots.telegrambotnutritionist.bot.service.manager.auth;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.enity.person.Role;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.factory.KeyboardFactory;
import com.bots.telegrambotnutritionist.bot.service.manager.AbstractManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthManager extends AbstractManager {
    final PersonRepository personRepository;
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    @Autowired
    public AuthManager(PersonRepository personRepository,
                       AnswerMethodFactory methodFactory,
                       KeyboardFactory keyboardFactory) {
        this.personRepository = personRepository;
        this.methodFactory = methodFactory;
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
        personRepository.save(person);
        return methodFactory.getSendMessage(
                chatId,
                """
                        Выберите свою роль, для дальнейшей авторизации:
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Клиент", "Администратор"),
                        List.of(2),
                        List.of(AUTH_CLIENT, AUTH_ADMIN)
                )
        );
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        // Формируем команды, которые есть у администратора или клиента
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        var person = personRepository.findById(chatId).orElseThrow();
        HashMap<String, String> commands = new HashMap<>();
        commands.put("start", "Нажмите, чтобы запустить бот");
        commands.put("menu", "Меню");
        commands.put("about", "Обо мне");
        commands.put("webinars", "Вебинары");
        commands.put("reviews", "Отзывы");
        commands.put("answer", "Ответы на вопросы");
        commands.put("support", "Сопровождение");
        commands.put("submit", "Оставить заявку");
        if (AUTH_ADMIN.equals(callbackQuery.getData())) { // добавить авторизацию для одного администратора!
            try {
                commands.put("listPerson", "Вывести всех пользователей");
                commands.put("listSupport", "Вывести список заявок на сопровждение");
                commands.put("listWebinars", "Вывести список вебинаров");
                commands.put("sales", "Вывести график продаж");
                bot.execute(methodFactory.getBotCommandScopeChat(
                        chatId,
                        commands)
                );
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
            person.setRole(Role.ADMIN);
        } else {
            try {
                bot.execute(methodFactory.getBotCommandScopeChat(
                        chatId,
                        commands)
                );
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
            person.setRole(Role.CLIENT);
        }
        person.setAction(Action.FREE); // освободился от действий так как выбрал роль
        personRepository.save(person);
        try {
            bot.execute(
                    methodFactory.getAnswerCallbackQuery(
                            callbackQuery.getId(),
                            """
                                 Авторизация прошла успешно, повторите предыдущий запрос
                                 """)
            );
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return methodFactory.getDeleteMessage(
                chatId, messageId);
    }
}