package com.bots.telegrambotnutritionist.bot.proxy;

import com.bots.telegrambotnutritionist.bot.enity.person.Action;
import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import com.bots.telegrambotnutritionist.bot.enity.person.Role;
import com.bots.telegrambotnutritionist.bot.repository.PersonRepository;
import com.bots.telegrambotnutritionist.bot.service.manager.auth.AuthManager;
import com.bots.telegrambotnutritionist.bot.telegram.Bot;
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

@Aspect
@Component
@Order(100)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthAspect {
    final PersonRepository personRepository;
    final AuthManager authManager;

    @Autowired
    public AuthAspect(PersonRepository personRepository,
                      AuthManager authManager) {
        this.personRepository = personRepository;
        this.authManager = authManager;
    }

    @Pointcut("execution(* com.bots.telegrambotnutritionist.bot.service.UpdateDispatcher.distribute(..))")
    public void distributeMethodPointCut() {
    }

    @Around("distributeMethodPointCut()")
    public Object authMethodAdvice(ProceedingJoinPoint joinPoint)
            throws Throwable {
        Update update = (Update) joinPoint.getArgs()[0];
        Person person;
        if (update.hasMessage()) {
            person = personRepository.findById(update.getMessage().getChatId()).orElseThrow();
        } else if (update.hasCallbackQuery()) {
            person = personRepository.findById(update.getCallbackQuery().getMessage().getChatId()).orElseThrow();
        } else {
            return joinPoint.proceed();
        }
        if (person.getRole() != Role.EMPTY) {
            return joinPoint.proceed();
        }
        if (person.getAction() == Action.AUTH) {
            return joinPoint.proceed();
        }
        return authManager.answerMessage(update.getMessage(),
                (Bot) joinPoint.getArgs()[1]);
    }
}