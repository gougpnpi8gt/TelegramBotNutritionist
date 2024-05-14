package com.bots.telegrambotnutritionist.bot.service.handler;

import com.bots.telegrambotnutritionist.bot.service.manager.aboutMe.AboutMeManager;
import com.bots.telegrambotnutritionist.bot.service.manager.auth.AuthManager;
import com.bots.telegrambotnutritionist.bot.service.manager.menu.MenuManager;
import com.bots.telegrambotnutritionist.bot.service.manager.review.ReviewManager;
import com.bots.telegrambotnutritionist.bot.service.manager.support.SupportManager;
import com.bots.telegrambotnutritionist.bot.service.manager.webinars.WebinarManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallBackQueryHandler {
    final AuthManager authManager;
    final SupportManager supportManager;
    final AboutMeManager aboutMe;
    final WebinarManager webinarManager;
    final ReviewManager reviewManager;
    final MenuManager menuManager;


    @Autowired
    public CallBackQueryHandler(AuthManager authManager,
                                SupportManager supportManager,
                                AboutMeManager aboutMe,
                                WebinarManager webinarManager,
                                ReviewManager reviewManager,
                                MenuManager menuManager
    ) {
        this.authManager = authManager;
        this.supportManager = supportManager;
        this.aboutMe = aboutMe;
        this.webinarManager = webinarManager;
        this.reviewManager = reviewManager;
        this.menuManager = menuManager;
    }

    public BotApiMethod<?> answer(CallbackQuery callBackQuery, Bot bot) {
        String callbackData = callBackQuery.getData();
        String keyWord = callbackData.split("_")[0];
        switch (keyWord){
            case AUTH -> {
                return authManager.answerCallbackQuery(callBackQuery, bot);
            }
        }
        switch (callbackData) {
            case MENU, START -> {
                return menuManager.answerCallbackQuery(callBackQuery, bot);
            }
            case ABOUT -> {
                return aboutMe.answerCallbackQuery(callBackQuery, bot);
            }
            case SUPPORT -> {
                return supportManager.answerCallbackQuery(callBackQuery, bot);
            }
            case WEBINARS -> {
                return webinarManager.answerCallbackQuery(callBackQuery, bot);
            }
            case REVIEWS -> {
                return reviewManager.answerCallbackQuery(callBackQuery, bot);
            }
        }
        return null;
    }
}
