package com.bots.telegrambotnutritionist.bot.service.manager.webinars;


import com.bots.telegrambotnutritionist.bot.enity.webinars.Webinar;
import com.bots.telegrambotnutritionist.bot.repository.WebinarRepository;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class WebinarManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    final WebinarRepository webinarRepository;
    @Autowired
    public WebinarManager(AnswerMethodFactory methodFactory,
                          KeyboardFactory keyboardFactory,
                          WebinarRepository webinarRepository) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.webinarRepository = webinarRepository;
    }
    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        List<Webinar> list = getFiles();
        Long chatId = message.getChatId();
        if (list.isEmpty()) {
            return methodFactory.getSendMessage(
                    chatId,
                    "Вебинаров пока нет",
                    keyboardFactory.getInlineKeyboard(
                            List.of("Вернитесь в меню"),
                            List.of(1),
                            List.of(MENU)
                    )
            );
        }
        List<String> text = new ArrayList<>();
        List<Integer> cfg = new ArrayList<>();
        List<String> data = new ArrayList<>();
        int index = 0;
        for (Webinar video : list) {
            String nameVideo = video.getName();
            text.add(nameVideo);
            data.add(WEBINARS + "_" + nameVideo.split(" ")[1]);
            if (index == 3) {
                cfg.add(3);
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
        return methodFactory.getSendMessage(
                chatId,
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
    private List<Webinar> getFiles(){
        List<Webinar> webinars = webinarRepository.findAll();
        if (webinars.isEmpty()) {
            File directoryFileWithVideos = new File("D:\\Projects");
            List<File> files = Arrays
                    .stream(directoryFileWithVideos.listFiles())
                    .filter(File::isFile)
                    .collect(Collectors.toList());
            if (!files.isEmpty()) {
                webinars = files.stream()
                        .map(file -> new Webinar(file.getName().substring(0, 9)))
                        .collect(Collectors.toList());
                webinarRepository.saveAll(webinars);
            }
        }
        return webinars;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String date = callbackQuery.getData();
        String[] callbackData = date.split("_");
        if (callbackData.length > 1){
            Webinar webinar = webinarRepository.findWebinarById(Integer.valueOf(callbackData[1]));
            return showWebinar(callbackQuery, bot, webinar);
        } else {
            List<Webinar> list = getFiles();
            if (list.isEmpty()) {
                return methodFactory.getEditMessageText(
                        callbackQuery,
                        "Вебинаров пока нет",
                        keyboardFactory.getInlineKeyboard(
                                List.of("Вернитесь в меню"),
                                List.of(1),
                                List.of(MENU)
                        )
                );
            }
            List<String> text = new ArrayList<>();
            List<Integer> cfg = new ArrayList<>();
            List<String> data = new ArrayList<>();
            int index = 0;
            for (Webinar video : list) {
                String nameVideo = video.getName();
                text.add(nameVideo);
                data.add(WEBINARS + "_" + nameVideo.split(" ")[1]);
                if (index == 3) {
                    cfg.add(3);
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

    private BotApiMethod<?> showWebinar(CallbackQuery callbackQuery, Bot bot, Webinar webinar) {
        if (webinar.isPay()) {
            try {
                bot.execute(methodFactory.getSendVideo(
                                callbackQuery.getMessage().getChatId(),
                                "D:\\Projects\\" + webinar.getName() + ".mp4",
                                webinar.getName(),
                                keyboardFactory.getInlineKeyboard(
                                        List.of("Открыть полный список вебинаров"),
                                        List.of(1),
                                        List.of(WEBINARS)
                                )
                        )
                );
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        } else {
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "Данный вебинар не оплачен, видео не доступно для просмотра",
                    keyboardFactory.getInlineKeyboard(
                            List.of( "Открыть полный список вебинаров"),
                            List.of(1),
                            List.of( WEBINARS)
                    )
            );
        }
        return null;
    }
}