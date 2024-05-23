package com.bots.telegrambotnutritionist.bot.enity.person;

import com.bots.telegrambotnutritionist.bot.enity.reviews.Reviews;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Person")
public class Person {

    @Id
    @Column(name = "id")
    Long id; // идентификатор чата для пользователя

    @Column(name = "token",
            unique = true)
    String token; // уникальный для каждого человека и его может будет поменять

    @Column(name = "Name")
    //@NotEmpty(message = "Имя не должно быть пустым")
    //@Size(min = 2, max = 20, message = "Имя не больше 20 символов")
    String name;

    @Column(name = "SurName")
    //@Size(min = 2, max = 20, message = "Фамилия не больше 20 символов")
    String surName;

    @Column(name = "Patronymic")
    //@Size(min = 2, max = 20, message = "Отчество не больше 20 символов")
    String patronymic;

    @Column(name = "Age")
    //@NotNull
    //@Min(value = 0, message = "Возраст начинается от 0")
   // @Max(value = 100, message = "Возраст не может быть больше 100 лет")
    int age;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(name = "Weight")
    //@Min(value = 0, message = "Вес начинается от 0")
    //@Max(value = 300, message = "Для ограничения ввода случайно большого числа.\\n " +
   //         "Вес не может быть больше 300 кг")
    int weight;

    @Column(name = "Birthday")
    //@Temporal(TemporalType.TIMESTAMP)
    LocalDateTime birthday;

    @Column(name = "Country")
   // @Size(min = 2, max = 20, message = "Не больше 20 символов")
   // @NotEmpty(message = "Поле не должно быть пустым")
    String country;

    @Column(name = "Characteristic")
   // @Size(min = 2, max = 1000, message = "Описание не больше 1000 символов")
    String characteristicsOfAPerson;

    //@Transient
    @Enumerated(EnumType.STRING)
    Action action;

    @Enumerated(EnumType.STRING)
    Role role;

    @OneToMany(mappedBy = "person")
    List<Reviews> reviews;

    @PrePersist
    private void generateUniqueToken() {
        if (token == null) {
            token = String.valueOf(UUID.randomUUID());
        }
    }
    public void addReviews(Reviews review) {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        reviews.add(review);
    }
    public void refreshToken() {
        token = String.valueOf(UUID.randomUUID());
    }
}
