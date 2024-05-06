package com.bots.telegrambotnutritionist.bot.proxy;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import com.bots.telegrambotnutritionist.bot.enity.person.Role;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Aspect
@Order(10) // так как аспекта два, то ставим очередность
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonCreateAspect {

    final PersonRepository personRepository;

    @Autowired
    public PersonCreateAspect(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // некоторый указатель перед каким методом будет выполняться совет (advice). (..) -аргументы
    // добавим в Application @EnableAspectAutoProxy чтобы класс работал и пишем сам совет
    @Pointcut("execution(* com.bots.telegrambotnutritionist.bot.service.UpdateDispatcher.distribute(..))")
    public void distributeMethodPointCut() {

    }

    @Around("distributeMethodPointCut()") // выполняется будет перед, вовремя и после, т.е. вокруг, будем иметь полный контроль над методом
    public Object distributeMethodAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        // ProceedingJoinPoint - точка входа, которая дает нам контроль над методом, из неё можно получить аргументы в виде массива объектов
        Update update = (Update) joinPoint.getArgs()[0];
        User telegramUser; // лежит пользователь
        if (update.hasMessage()) {
            telegramUser = update.getMessage().getFrom();
            // getFrom - из сообщения получить пользователя, т.е. от кого оно отправлено
        } else if (update.hasCallbackQuery()) {
            telegramUser = update.getCallbackQuery().getFrom();
        } else {
            // исключение, те случаи когда обновление не прошло(пользователь заблокировал чат бота,  новый чат пользователь)
            return joinPoint.proceed();
            // мы позволяем методу distribute выполниться, есть пользователь или нет - неважно
        }
        if (personRepository.existsById(telegramUser.getId())){
            return joinPoint.proceed();
        }
        Person person =
                Person.builder()
                        .id(telegramUser.getId())
                        .action(Action.FREE)
                        .role(Role.EMPTY)
                        .build();
        personRepository.save(person);
        return joinPoint.proceed();
    }
}
