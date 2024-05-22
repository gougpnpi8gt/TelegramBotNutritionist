package com.bots.telegrambotnutritionist.bot.util;

import com.bots.telegrambotnutritionist.bot.enity.textMenu.TextMenu;
import com.bots.telegrambotnutritionist.bot.repository.TextInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DescriptionCommands {
    private final TextInformation textInformation;

    @Autowired
    public DescriptionCommands(TextInformation textInformation) {
        this.textInformation = textInformation;
    }

    public List<TextMenu> getMenuList() {
        List<TextMenu> textMenus = new ArrayList<>();
        textMenus.add(new TextMenu("start", "Нажмите, чтобы запустить бот"));
        textMenus.add(new TextMenu("menu", "Меню"));
        textMenus.add(new TextMenu("about", "Обо мне"));
        textMenus.add(new TextMenu("webinars", "Вебинары"));
        textMenus.add(new TextMenu("reviews", "Отзывы"));
        textMenus.add(new TextMenu("answer", "Ответы на вопросы"));
        textMenus.add(new TextMenu("support", "Сопровождение"));
        textMenus.add(new TextMenu("submit", "Оставить заявку"));
        textInformation.saveAll(textMenus);
        return textMenus;
    }

    public Map<String, String> builtFirstCommand() {
        Map<String, String> commands = new HashMap<>();
        List<TextMenu> list = getMenuList();
        for (TextMenu textMenu : list) {
            commands.put(textMenu.getKeyInfo(), textMenu.getTextInfo());
        }
        return commands;
    }

    public void addCommand(String command, String text) {
        textInformation.save(new TextMenu(command, text));
    }

    public void updateTextCommand(String command, String text) {
        TextMenu textMenu = textInformation.findByKeyInfo(command);
        textMenu.setTextInfo(text);
        textInformation.save(textMenu);
    }

    public void delete(String keyInfo) {
        textInformation.deleteByKeyInfo(keyInfo);
    }

    public Map<String, String> adminCommands() {
        List<TextMenu> list = getMenuList();;
        textInformation.deleteAll();
        list.add(new TextMenu("ListPersons", "выводит список пользователей"));
        list.add(new TextMenu("ListSupports", "выводит список заявок на сопровождение"));
        list.add(new TextMenu("ListWebinars", "выводит список вебинаров"));
        list.add(new TextMenu("ListPricesWebinars", "выводит список цен вебинаров"));
        list.add(new TextMenu("Sales", "график продаж"));
        textInformation.saveAll(list);
        Map<String, String> commands = new HashMap<>();
        for (TextMenu textMenu : list) {
            commands.put(textMenu.getKeyInfo(), textMenu.getTextInfo());
        }
        return commands;
    }
}
