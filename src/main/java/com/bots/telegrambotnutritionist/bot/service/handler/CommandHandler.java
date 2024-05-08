package com.bots.telegrambotnutritionist.bot.service.handler;

import com.bots.telegrambotnutritionist.bot.service.manager.aboutMe.AboutMeManager;
import com.bots.telegrambotnutritionist.bot.service.manager.admin.AdminManager;
import com.bots.telegrambotnutritionist.bot.service.manager.menu.MenuManager;
import com.bots.telegrambotnutritionist.bot.service.manager.review.ReviewManager;
import com.bots.telegrambotnutritionist.bot.service.manager.support.SupportManager;
import com.bots.telegrambotnutritionist.bot.service.manager.start.StartManager;
import com.bots.telegrambotnutritionist.bot.service.manager.webinars.WebinarManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.bots.telegrambotnutritionist.bot.service.data.Command.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommandHandler {
    final StartManager startManager;
    final SupportManager supportManager;
    final ReviewManager reviewManager;
    final WebinarManager webinarManager;
    final AboutMeManager aboutMeManager;
    final MenuManager menuManager;
    final AdminManager adminManager;


    @Autowired
    public CommandHandler(StartManager startManager,
                          SupportManager supportManager,
                          ReviewManager reviewManager,
                          WebinarManager webinarManager,
                          AboutMeManager aboutMeManager,
                          MenuManager menuManager,
                          AdminManager adminManager
    ) {
        this.startManager = startManager;
        this.reviewManager = reviewManager;
        this.webinarManager = webinarManager;
        this.aboutMeManager = aboutMeManager;
        this.menuManager = menuManager;
        this.supportManager = supportManager;
        this.adminManager = adminManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        String command = message.getText();
        switch (command) {
            case START -> {
                return startManager.answerCommand(message, bot);
            }
            case MENU -> {
                return menuManager.answerCommand(message, bot);
            }
            case SUPPORT -> {
                return supportManager.answerCommand(message, bot);
            }
            case REVIEWS -> {
                return reviewManager.answerCommand(message, bot);
            }
            case ABOUT -> {
                return aboutMeManager.answerCommand(message, bot);
            }
            case ADMIN -> {
                return adminManager.answerCommand(message, bot);
            }
            default -> {
                return defaultAnswer(message);
            }
        }
    }

    private BotApiMethod<?> defaultAnswer(Message message) {
        return SendMessage.builder().text("""
                        Неподдерживаемая команда
                        """)
                .chatId(message.getChatId())
                .build();
    }
}
