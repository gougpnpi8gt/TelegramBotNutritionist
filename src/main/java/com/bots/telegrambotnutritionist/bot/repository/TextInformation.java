package com.bots.telegrambotnutritionist.bot.repository;

import com.bots.telegrambotnutritionist.bot.enity.textMenu.TextMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextInformation extends JpaRepository<TextMenu, Integer> {
    void deleteByKeyInfo(String keyInfo);
    TextMenu findByKeyInfo(String keyInfo);
}
