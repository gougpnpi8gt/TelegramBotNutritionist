package com.bots.telegrambotnutritionist.bot.service.manager.webinars;


import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.factory.KeyboardFactory;
import com.bots.telegrambotnutritionist.bot.service.manager.AbstractManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class WebinarManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    @Autowired
    public WebinarManager(AnswerMethodFactory methodFactory,
                        KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }
    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        List<Message> list = null;
        try {
            list = bot.execute(methodFactory.getSendMediaGroup(message.getChatId()));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        List<String> text = new ArrayList<>();
        List<Integer> cfg = new ArrayList<>();
        List<String> data = new ArrayList<>();
        int index = 0;
        if (list != null) {
            for (Message video : list) {
                String nameVideo = video.getVideo().getFileName(); //  в каталоге файлы хранятся в виде - Вебинар 1, Вебинар 2 и т.д.
                text.add(nameVideo);
                data.add(WEBINARS  + "_" + nameVideo.split(" ")[1]);
                if (index == 5) {
                    cfg.add(5);
                    index = 0;
                } else {
                    index += 1;
                }
            }
        }
        if (index != 0) {
            cfg.add(index);
        }
        text.add("Меню");
        cfg.add(1);
        data.add(MENU);
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                        Вебинары - это интерактивные онлайн-мероприятия, где я, ведущий нутрициолог, делюсь с вами самой актуальной информацией о здоровом питании и образе жизни.
                        Здесь вы узнаете о секретах правильного питания, получите ценные советы по достижению своих здоровых целей.
                        Мои вебинары - это возможность для вас узнать, как сделать ваш рацион более здоровым, энергичным и удовлетворительным.
                        Никаких скучных лекций - только практические советы, которые вы сможете применить в своей повседневной жизни.
                        Присоединяйтесь к нашим вебинарам и сделайте первый шаг к лучшему здоровью уже сегодня!
                        \s
                        """,
                keyboardFactory.getInlineKeyboard(
                        text,
                        cfg,
                        data
                )
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callBackData = callbackQuery.getData();
        String[] array = callBackData.split("_");
        List<Message> list = null;
        try {
            list = bot.execute(methodFactory.getSendMediaGroup(callbackQuery.getMessage().getChatId()));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        List<String> text = new ArrayList<>();
        List<Integer> cfg = new ArrayList<>();
        List<String> data = new ArrayList<>();
        int index = 0;
        for (Message video : list) {
            String nameVideo = video.getVideo().getFileName(); //  в каталоге файлы хранятся в виде - Вебинар 1, Вебинар 2 и т.д.
            text.add(nameVideo);
            data.add(WEBINARS  + "_" + nameVideo.split(" ")[1]);
            if (index == 5) {
                cfg.add(5);
                index = 0;
            } else {
                index += 1;
            }
        }
        if (index != 0) {
            cfg.add(index);
        }
        text.add("Меню");
        cfg.add(1);
        data.add(MENU);
        switch (callBackData){
            case NEXT -> {
                return nextVideo(callbackQuery, bot, callBackData + 1);
            }
            case PREV -> {
                return prevVideo(callbackQuery, bot, callBackData);
            }
        }
        if (array.length > 1){
            return showWebinar(callbackQuery, bot, callBackData);
        } else {
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    """
                            Вебинары - это интерактивные онлайн-мероприятия, где я, ведущий нутрициолог, делюсь с вами самой актуальной информацией о здоровом питании и образе жизни.
                            Здесь вы узнаете о секретах правильного питания, получите ценные советы по достижению своих здоровых целей.
                            Мои вебинары - это возможность для вас узнать, как сделать ваш рацион более здоровым, энергичным и удовлетворительным.
                            Никаких скучных лекций - только практические советы, которые вы сможете применить в своей повседневной жизни.
                            Присоединяйтесь к нашим вебинарам и сделайте первый шаг к лучшему здоровью уже сегодня!
                            \s
                            """,
                    keyboardFactory.getInlineKeyboard(
                            text,
                            cfg,
                            data
                    )
            );
        }
    }

    private BotApiMethod<?> prevVideo(CallbackQuery callbackQuery, Bot bot, String nameWebinar) {
        try {
            bot.execute(methodFactory.getEditMessageVideo(
                    callbackQuery,
                    "D:\\Projects\\" + nameWebinar,
                    keyboardFactory.getInlineKeyboard(
                            List.of("◀\uFE0F", "▶\uFE0F", "Меню"),
                            List.of(2, 1),
                            List.of(PREV, NEXT, MENU)
                    )
            ));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private BotApiMethod<?> nextVideo(CallbackQuery callbackQuery, Bot bot, String nameWebinar) {
        try {
            bot.execute(methodFactory.getEditMessageVideo(
                    callbackQuery,
                    "D:\\Projects\\" + nameWebinar,
                    keyboardFactory.getInlineKeyboard(
                            List.of("◀\uFE0F", "▶\uFE0F", "Меню"),
                            List.of(2, 1),
                            List.of(PREV, NEXT, MENU)
                    )
            ));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private BotApiMethod<?> showWebinar(CallbackQuery callbackQuery, Bot bot, String nameWebinar) {
        try {
             bot.execute(methodFactory.getSendVideo(
                            callbackQuery.getMessage().getChatId(),
                            "D:\\Projects\\" + nameWebinar,
                            keyboardFactory.getInlineKeyboard(
                                    List.of("◀\uFE0F", "▶\uFE0F", "Меню"),
                                    List.of(2, 1),
                                    List.of(PREV, NEXT, MENU)
                            )
                    )
            );
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}