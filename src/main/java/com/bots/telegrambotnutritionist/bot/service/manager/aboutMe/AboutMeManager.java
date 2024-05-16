package com.bots.telegrambotnutritionist.bot.service.manager.aboutMe;

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
public class AboutMeManager extends AbstractManager {
    final KeyboardFactory keyboardFactory;
    final AnswerMethodFactory methodFactory;

    @Autowired
    public AboutMeManager(KeyboardFactory keyboardFactory,
                          AnswerMethodFactory methodFactory) {
        this.keyboardFactory = keyboardFactory;
        this.methodFactory = methodFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                            Привет! Меня зовут Екатерина, и я нутрициолог с большой страстью к помощи людям в достижении оптимального здоровья через правильное питание.
                            Я будущий врач, обучающийся в престижном медицинском университете имени Алмазова, и я стремлюсь к глубокому пониманию того, как питание влияет на наше здоровье и благополучие.\n" +
                           "В свои 21 год я уже завершила профильные курсы по нутрициологии, где приобрела фундаментальные знания и навыки в области здорового питания.
                            Моя цель - помогать людям достичь своих здоровых целей, разрабатывая индивидуальные планы питания и предоставляя ценные советы по повышению качества жизни.\n" +
                           "С учетом моего образования и опыта, я готова предложить вам компетентное и заботливое партнерство в вашем путешествии к здоровому образу жизни.
                            Доверьтесь мне, и вместе мы достигнем ваших целей и сделаем ваше питание здоровым, вкусным и удовлетворяющим!\n
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Вебинары", "Сопровождение", "В меню"),
                        List.of(2, 1),
                        List.of(WEBINARS, SUPPORT, MENU)
                )
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?>  answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return methodFactory.getEditMessageText(callbackQuery,
                """
                            Привет! Меня зовут Екатерина, и я нутрициолог с большой страстью к помощи людям в достижении оптимального здоровья через правильное питание.
                            Я будущий врач, обучающийся в престижном медицинском университете имени Алмазова, и я стремлюсь к глубокому пониманию того, как питание влияет на наше здоровье и благополучие.\n" +
                           "В свои 21 год я уже завершила профильные курсы по нутрициологии, где приобрела фундаментальные знания и навыки в области здорового питания.
                            Моя цель - помогать людям достичь своих здоровых целей, разрабатывая индивидуальные планы питания и предоставляя ценные советы по повышению качества жизни.\n" +
                           "С учетом моего образования и опыта, я готова предложить вам компетентное и заботливое партнерство в вашем путешествии к здоровому образу жизни.
                            Доверьтесь мне, и вместе мы достигнем ваших целей и сделаем ваше питание здоровым, вкусным и удовлетворяющим!\n
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Вебинары", "Сопровождение", "В меню"),
                        List.of(2, 1),
                        List.of(WEBINARS, SUPPORT, MENU)
                )
        );
    }
}
