package com.bots.telegrambotnutritionist.bot.service.manager.admin;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import com.bots.telegrambotnutritionist.bot.enity.person.Role;
import com.bots.telegrambotnutritionist.bot.enity.textMenu.TextMenu;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.repository.TextInformation;
import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.factory.KeyboardFactory;
import com.bots.telegrambotnutritionist.bot.service.manager.AbstractManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import com.bots.telegrambotnutritionist.bot.util.DescriptionCommands;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.ADMIN_CANCEL;
import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.MENU;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final TextInformation textInformation;
    final PersonRepository personRepository;
    final DescriptionCommands commands;
    final KeyboardFactory keyboardFactory;
    static int countTheAdmin = 0;

    @Value("${unique.password}")
    String password;

    @Autowired
    public AdminManager(AnswerMethodFactory methodFactory,
                        TextInformation textInformation,
                        PersonRepository personRepository,
                        DescriptionCommands commands,
                        KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.textInformation = textInformation;
        this.personRepository = personRepository;
        this.commands = commands;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        List<TextMenu> textMenus;
        if (countTheAdmin == 0){
            textMenus = commands.adminList();
            Map<String, String> map = commands.adminCommands();
            try {
                bot.execute(methodFactory.getDeleteMyCommands(chatId));
                bot.execute(methodFactory.getBotCommandScopeChat(
                        chatId,
                        map)
                );
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
            countTheAdmin++;
        } else {
            textMenus = textInformation.findAll();
        }
        StringBuilder builder = new StringBuilder();
        textMenus
                .stream()
                .map(TextMenu::toString)
                .forEach(string -> builder.append(string).append("\n"));
        return methodFactory.getSendMessage(
                chatId,
                STR."Привет! Чем могу служить? \n Список доступных команд: \n \{builder}",//?
                null
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        Person person = personRepository.findPersonById(chatId);
        String myPassword = message.getText();
        if (myPassword.equals(password)){
            person.setToken(myPassword);
            person.setRole(Role.ADMIN);
            person.setAction(Action.FREE);
            personRepository.save(person);
            return methodFactory.getSendMessage(
                    chatId,
                    "Пароль верный, нажмите еще раз команду /admin",
                    null);
        } else {
            return methodFactory.getSendMessage(
                    chatId,
                    "Пароль неверный, попробуй еще раз",
                    keyboardFactory.getInlineKeyboard(
                            List.of("Вернуться в меню"),
                            List.of(1),
                            List.of(ADMIN_CANCEL)
                    )
            );
        }
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        if (callbackQuery.getData().equals(ADMIN_CANCEL)){
            Person person = personRepository.findPersonById(callbackQuery.getMessage().getChatId());
            person.setAction(Action.FREE);
            personRepository.save(person);
        }
        StringBuilder builder = new StringBuilder();
        textInformation.findAll()
                .stream()
                .map(TextMenu::toString)
                .forEach(string -> builder.append(string).append("\n"));
        return methodFactory.getEditMessageText(
                callbackQuery,
                STR."Привет! Чем могу служить?\n Список доступных команд: \n \{builder}",
                null
        );
    }
}