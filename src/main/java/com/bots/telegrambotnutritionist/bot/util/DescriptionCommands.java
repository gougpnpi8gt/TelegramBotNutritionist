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
    private static int contactingTheAdmin = 0;

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
    public List<TextMenu> adminList(){
        List<TextMenu> list = textInformation.findAll();
        if (contactingTheAdmin == 0){
            List<TextMenu> dopList = new ArrayList<>();
            dopList.add(new TextMenu("admin", "страничка администратора"));
            dopList.add(new TextMenu("list_persons", "выводит список пользователей"));
            dopList.add(new TextMenu("list_supports", "выводит список заявок на сопровождение"));
            dopList.add(new TextMenu("list_webinars", "выводит список вебинаров"));
            dopList.add(new TextMenu("list_price_webinars", "выводит список цен вебинаров"));
            dopList.add(new TextMenu("sales", "график продаж"));
            dopList.add(new TextMenu("editor", "меню редактирования команд"));
            textInformation.saveAll(dopList);
            contactingTheAdmin++;
            list.addAll(dopList);
            return list;
        } else {
            return list;
        }
    }

    public Map<String, String> adminCommands() {
        List<TextMenu> list = adminList();
        Map<String, String> commands = new HashMap<>();
        for (TextMenu textMenu : list) {
            commands.put(textMenu.getKeyInfo(), textMenu.getTextInfo());
        }
        return commands;
    }
}
