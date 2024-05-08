package com.bots.telegrambotnutritionist.bot.proxy;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.util.DescriptionCommands;
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
@Order(10)
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonCreateAspect {
    final PersonRepository personRepository;

    @Autowired
    public PersonCreateAspect(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Pointcut("execution(* com.bots.telegrambotnutritionist.bot.service.UpdateDispatcher.distribute(..))")
    public void distributeMethodPointCut() {

    }

    @Around("distributeMethodPointCut()")
    public Object distributeMethodAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Update update = (Update) joinPoint.getArgs()[0];
        User telegramUser;
        if (update.hasMessage()) {
            telegramUser = update.getMessage().getFrom();
        } else if (update.hasCallbackQuery()) {
            telegramUser = update.getCallbackQuery().getFrom();
        } else {
            return joinPoint.proceed();
        }
        if (personRepository.existsById(telegramUser.getId())){
            return joinPoint.proceed();
        }
        Person person =
                Person.builder()
                        .id(telegramUser.getId())
                        .action(Action.FREE)
                        .build();
        personRepository.save(person);
        return joinPoint.proceed();
    }
}
