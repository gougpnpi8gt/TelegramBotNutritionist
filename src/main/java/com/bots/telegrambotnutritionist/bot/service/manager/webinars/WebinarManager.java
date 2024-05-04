package com.bots.telegrambotnutritionist.bot.service.manager.webinars;


import com.bots.telegrambotnutritionist.bot.service.factory.AnswerMethodFactory;
import com.bots.telegrambotnutritionist.bot.service.factory.KeyboardFactory;
import com.bots.telegrambotnutritionist.bot.service.manager.AbstractManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.WEBINARS;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
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
                        List.of("Вебинар №" + "будет автоматически инкерементироваться в зависимости от количества медиа-файлов"),
                        List.of(1),
                        List.of(WEBINARS)
                )
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
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
                        List.of("Вебинар №" + "будет автоматически инкерементироваться в зависимости от количества медиа-файлов"),
                        List.of(1),
                        List.of(WEBINARS)
                )
        );
    }
}
