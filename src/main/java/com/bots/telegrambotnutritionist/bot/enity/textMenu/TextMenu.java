package com.bots.telegrambotnutritionist.bot.enity.textMenu;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "text_information_menu")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TextMenu {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @Column(name = "key")
    String keyInfo;

    @Column(name = "text")
    String textInfo;

    public TextMenu(String keyInfo, String textInfo) {
        this.keyInfo = keyInfo;
        this.textInfo = textInfo;
    }

    public TextMenu() {

    }

    @Override
    public String toString() {
        return "/" + keyInfo + " - " + textInfo + ";";
    }
}