package com.bots.telegrambotnutritionist.bot.service.manager.editor;

import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.factory.KeyboardFactory;
import com.bots.telegrambotnutritionist.bot.service.manager.AbstractManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import com.bots.telegrambotnutritionist.bot.util.DescriptionCommands;
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
public class EditorManager extends AbstractManager {
    final KeyboardFactory keyboardFactory;
    final AnswerMethodFactory methodFactory;
    final DescriptionCommands descriptionCommands;
    @Autowired
    public EditorManager(KeyboardFactory keyboardFactory,
                         AnswerMethodFactory methodFactory,
                         DescriptionCommands descriptionCommands) {
        this.keyboardFactory = keyboardFactory;
        this.methodFactory = methodFactory;
        this.descriptionCommands = descriptionCommands;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                        Меню редактирования команд
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Добавить команду", "Удалить команду",
                                "Обновить описание команды", "Вернуться обратно в меню"),
                        List.of(2, 1, 1),
                        List.of(ADD_COMMAND, DELETE_COMMAND, UPDATE_COMMAND, ADMIN)
                )
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String data = callbackQuery.getData();
        switch (data){
            case ADD_COMMAND ->{
                return addCommand();
            }
        }
        return null;
    }

    private BotApiMethod<?> addCommand() {
        return null;
    }
}
