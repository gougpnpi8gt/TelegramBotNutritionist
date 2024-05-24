package com.bots.telegrambotnutritionist.bot.service.manager.editor;

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
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditorManager extends AbstractManager {
    final KeyboardFactory keyboardFactory;
    final AnswerMethodFactory methodFactory;
    final DescriptionCommands descriptionCommands;
    final PersonRepository personRepository;

    @Autowired
    public EditorManager(KeyboardFactory keyboardFactory,
                         AnswerMethodFactory methodFactory,
                         DescriptionCommands descriptionCommands,
                         PersonRepository personRepository) {
        this.keyboardFactory = keyboardFactory;
        this.methodFactory = methodFactory;
        this.descriptionCommands = descriptionCommands;
        this.personRepository = personRepository;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return mainMenu(message);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        Person person = personRepository.findPersonById(chatId);
        if (message.hasText()) {
            String text = message.getText();
            switch (person.getAction()) {
                case Action.ADD_COMMAND -> {
                    String[] command = text.split("-");
                    descriptionCommands.addCommand(command[0], command[1]);
                    person.setAction(Action.FREE);
                    personRepository.save(person);
                    return methodFactory.getSendMessage(chatId,
                            "Команда добавлена",
                            getInlineKeyboardForMainEditor()
                    );
                }
                case Action.DELETE_COMMAND -> {
                    descriptionCommands.delete(text);
                    person.setAction(Action.FREE);
                    personRepository.save(person);
                    return methodFactory.getSendMessage(chatId,
                            "Команда удалена",
                            getInlineKeyboardForMainEditor()
                    );
                }
                case Action.UPDATE_COMMAND -> {
                    String[] command = text.split("-");
                    descriptionCommands.updateTextCommand(command[0], command[1]);
                    person.setAction(Action.FREE);
                    personRepository.save(person);
                    return methodFactory.getSendMessage(chatId,
                            "Команда обновлена",
                            getInlineKeyboardForMainEditor()
                    );
                }
            }
        } else {
            return methodFactory.getSendMessage(chatId,
                    "Сообщение не содержит текст, напишите снова пожалуйста",
                    null);
        }
        return null;
    }

    public InlineKeyboardMarkup getInlineKeyboardForMainEditor() {
        return keyboardFactory.getInlineKeyboard(
                List.of("Нажмите, чтобы вернуться в меню редактирования команд"),
                List.of(1),
                List.of(COMMAND_MENU)
        );
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String data = callbackQuery.getData();
        switch (data) {
            case COMMAND_ADD -> {
                return addCommand(callbackQuery);
            }
            case COMMAND_DELETE -> {
                return deleteCommand(callbackQuery);
            }
            case COMMAND_UPDATE -> {
                return updateCommand(callbackQuery);
            }
            case COMMAND_MENU -> {
                return mainMenu(callbackQuery);
            }
        }
        return null;
    }

    private BotApiMethod<?> mainMenu(Message message) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                "Меню редактирования сообщений",
                getKeyboardMainMenu()
        );
//        return methodFactory.getSendMessage(
//                message.getChatId(),
//                """
//                        Меню редактирования команд
//                        """,
//                getKeyboardMainMenu()
//        );
    }

    private BotApiMethod<?> mainMenu(CallbackQuery callbackQuery) {
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Меню редактирования сообщений",
                getKeyboardMainMenu()
        );
//        return methodFactory.getEditMessageText(
//                callbackQuery,
//                """
//                        Меню редактирования команд
//                        """,
//                getKeyboardMainMenu()
//        );
    }
    private InlineKeyboardMarkup getKeyboardMainMenu(){
        return keyboardFactory.getInlineKeyboard(
                List.of("Добавить команду", "Удалить команду",
                        "Обновить описание команды", "Вернуться обратно в меню администратора"),
                List.of(2, 1),
                List.of(COMMAND_ADD, COMMAND_UPDATE, ADMIN)
        );
//        return keyboardFactory.getInlineKeyboard(
//                List.of("Добавить команду", "Удалить команду",
//                        "Обновить описание команды", "Вернуться обратно в меню"),
//                List.of(2, 1, 1),
//                List.of(COMMAND_ADD, COMMAND_DELETE, COMMAND_UPDATE, MENU)
//        );
    }

    private BotApiMethod<?> getNoRights(CallbackQuery callbackQuery) {
        return methodFactory.getEditMessageText(
                callbackQuery,
                "У вас нет прав, для изменения меню команд",
                getInlineKeyboardForMainEditor()
        );
    }

    private BotApiMethod<?> updateCommand(CallbackQuery callbackQuery) {
        var person = contactingTheDatabaseForaPerson(callbackQuery);
        if (person.getRole() == Role.ADMIN) {
            person.setAction(Action.UPDATE_COMMAND);
            personRepository.save(person);
            return methodFactory.getEditMessageText(callbackQuery,
                    "Какую команду вы желаете заменить? \n " +
                            "Напишите название существующей команды и через '-' написать её описание",
                    null);
        } else {
            return getNoRights(callbackQuery);
        }
    }

    private BotApiMethod<?> deleteCommand(CallbackQuery callbackQuery) {
        var person = contactingTheDatabaseForaPerson(callbackQuery);
        if (person.getRole() == Role.ADMIN) {
            person.setAction(Action.DELETE_COMMAND);
            personRepository.save(person);
            return methodFactory.getEditMessageText(callbackQuery,
                    "Напишите название команды и она будет удалена из БД у всех пользователей",
                    null);
        } else {
            return getNoRights(callbackQuery);
        }
    }

    private BotApiMethod<?> addCommand(CallbackQuery callbackQuery) {
        var person = contactingTheDatabaseForaPerson(callbackQuery);
        if (person.getRole() == Role.ADMIN) {
            person.setAction(Action.ADD_COMMAND);
            personRepository.save(person);
            return methodFactory.getEditMessageText(callbackQuery,
                    "Отправьте название новой команды и её описание ввиде: " +
                            "name-description", null);
        } else {
            return getNoRights(callbackQuery);
        }
    }

    private Person contactingTheDatabaseForaPerson(CallbackQuery callbackQuery) { // Переводится как "обращение к базе за человеком"
        return personRepository.findPersonById(callbackQuery.getMessage().getChatId());
    }
}