package com.bots.telegrambotnutritionist.bot.enity.webinars;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Webinars")
@Entity
public class Webinar {
    @Id
    @Column(name = "Webinar_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "is_pay")
    boolean isPay = false;

    public Webinar(String name) {
        this.name = name;
    }
    public Webinar() {

    }
}
