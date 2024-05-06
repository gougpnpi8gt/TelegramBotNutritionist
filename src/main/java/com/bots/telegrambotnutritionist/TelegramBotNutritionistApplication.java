package com.bots.telegrambotnutritionist;

import com.bots.telegrambotnutritionist.bot.telegram.TelegramProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableConfigurationProperties(TelegramProperties.class)
@EnableAspectJAutoProxy
public class TelegramBotNutritionistApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotNutritionistApplication.class, args);
    }
}
