package com.bots.telegrambotnutritionist.bot.service.manager.support;

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

import static com.bots.telegrambotnutritionist.bot.service.data.CallBackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    @Autowired
    public SupportManager(AnswerMethodFactory methodFactory,
                          KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                        Мое месячное сопровождение - это индивидуализированный подход к вашему здоровому питанию, который помогает вам достичь ваших целей в короткие сроки.
                         В течение месяца я буду вашим гидом и наставником в путешествии к здоровому образу жизни.
                        В рамках программы сопровождения я предоставляю:
                           Индивидуальное Консультирование: Персональные встречи или консультации онлайн, где мы обсудим ваши цели, привычки питания и разработаем план действий на месяц вперед.
                           Подготовка Рациона: Разработка индивидуализированного рациона питания, учитывающего ваши предпочтения, потребности и цели.
                           Еженедельное Отслеживание Прогресса: Регулярные отчеты о вашем прогрессе, корректировка рациона в зависимости от изменений и обсуждение любых вопросов или проблем, которые могут возникнуть.
                           Поддержка и Мотивация: Постоянная поддержка и мотивация на протяжении всего месяца, чтобы помочь вам преодолеть препятствия и добиться успеха.
                        Мое месячное сопровождение - это ваш путь к здоровому питанию и образу жизни.
                         Доверьтесь мне, и вместе мы сделаем ваше питание более сбалансированным, здоровым и приятным!
                                                """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Ответы на вопросы", "Оставить заявку", "Вебинары","\uD83D\uDCD5 В меню"),
                        List.of(2, 1, 1),
                        List.of(ANSWER, SUBMIT, WEBINARS, MENU)
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
                        Мое месячное сопровождение - это индивидуализированный подход к вашему здоровому питанию, который помогает вам достичь ваших целей в короткие сроки.
                         В течение месяца я буду вашим гидом и наставником в путешествии к здоровому образу жизни.
                        В рамках программы сопровождения я предоставляю:
                           Индивидуальное Консультирование: Персональные встречи или консультации онлайн, где мы обсудим ваши цели, привычки питания и разработаем план действий на месяц вперед.
                           Подготовка Рациона: Разработка индивидуализированного рациона питания, учитывающего ваши предпочтения, потребности и цели.
                           Еженедельное Отслеживание Прогресса: Регулярные отчеты о вашем прогрессе, корректировка рациона в зависимости от изменений и обсуждение любых вопросов или проблем, которые могут возникнуть.
                           Поддержка и Мотивация: Постоянная поддержка и мотивация на протяжении всего месяца, чтобы помочь вам преодолеть препятствия и добиться успеха.
                        Мое месячное сопровождение - это ваш путь к здоровому питанию и образу жизни.
                         Доверьтесь мне, и вместе мы сделаем ваше питание более сбалансированным, здоровым и приятным!
                                                """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Ответы на вопросы", "Оставить заявку", "Вебинары","\uD83D\uDCD5 В меню"),
                        List.of(2, 1, 1),
                        List.of(ANSWER, SUBMIT, WEBINARS, MENU)
                )
        );
    }
}
