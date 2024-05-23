package com.bots.telegrambotnutritionist.bot.service.manager.admin;

import com.bots.telegrambotnutritionist.bot.enity.person.Role;
import com.bots.telegrambotnutritionist.bot.enity.textMenu.TextMenu;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.repository.TextInformation;
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

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final TextInformation textInformation;
    final PersonRepository personRepository;
    final DescriptionCommands commands;
    static int countTheAdmin = 0;
    @Autowired
    public AdminManager(AnswerMethodFactory methodFactory,
                        TextInformation textInformation,
                        PersonRepository personRepository,
                        DescriptionCommands commands) {
        this.methodFactory = methodFactory;
        this.textInformation = textInformation;
        this.personRepository = personRepository;
        this.commands = commands;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        Long chatId = message.getChatId();
        var person = personRepository.findById(chatId).orElseThrow();
        if (person.getRole().equals(Role.CLIENT)){
            person.setRole(Role.ADMIN);
            personRepository.save(person);
        }
        List<TextMenu> textMenus = textInformation.findAll();
        if (countTheAdmin == 0){
//            Map<String, String> map = commands.adminCommands();
//            try {
//                bot.execute(methodFactory.getDeleteMyCommands(chatId));
//                bot.execute(methodFactory.getBotCommandScopeChat(
//                        chatId,
//                        map)
//                );
//            } catch (TelegramApiException e) {
//                log.error(e.getMessage());
//            }
            countTheAdmin++;
        }
        StringBuilder builder = new StringBuilder();
        textMenus
                .stream()
                .map(TextMenu::toString)
                .forEach(string -> builder.append(string).append("\n"));
          /* если после редактирования команд, мы снова обратимся по команде /admin,
        то получим динамически изменяющийся список и его отображение в сообщении чата
         */
        return methodFactory.getSendMessage(
                chatId,
                STR."Привет! Чем могу служить? \n Список доступных команд: \n \{builder}",//?
                null
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        StringBuilder builder = new StringBuilder();
        textInformation.findAll()
                .stream()
                .map(TextMenu::toString)
                .forEach(string -> builder.append(string).append("\n"));
        return methodFactory.getEditMessageText(
                callbackQuery,
                STR."Привет! Чем могу служить?\n Список доступных команд: \n \{builder}",//?
                null
        );
    }
}