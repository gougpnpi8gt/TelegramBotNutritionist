package com.bots.telegrambotnutritionist.bot.enity.person;

import com.bots.telegrambotnutritionist.bot.enity.reviews.Reviews;
import com.bots.telegrambotnutritionist.bot.enity.support.Support;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
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
    Long id;

    @Column(name = "token",
            unique = true)
    String token;

    @Column(name = "name")
    @Size(min = 2, max = 20, message = "Имя не больше 20 символов")
    String name;

    @Column(name = "sur_name")
    @Size(min = 2, max = 20, message = "Фамилия не больше 20 символов")
    String surName;

    @Column(name = "patronymic")
    @Size(min = 2, max = 20, message = "Отчество не больше 20 символов")
    String patronymic;

    @Column(name = "age")
    @PositiveOrZero
    @Max(value = 100, message = "Возраст не может быть больше 100 лет")
    int age;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(name = "weight")
    @PositiveOrZero
    @Max(value = 300, message = "Вес не может быть больше 300 кг")
    int weight;

    @Column(name = "birthday")
    @Temporal(TemporalType.DATE)
    LocalDate birthday;

    @Column(name = "country")
    @Size(min = 2, max = 20, message = "Не больше 20 символов")
    String country;

    @Column(name = "characteristic")
    @Size(min = 2, max = 5000, message = "Описание не больше 5000 символов")
    String characteristicsOfAPerson;

    @Enumerated(EnumType.STRING)
    Action action;

    @Enumerated(EnumType.STRING)
    Role role;

    @OneToMany(mappedBy = "person")
    List<Reviews> reviews;

    @OneToMany(mappedBy = "person")
    List<Support> supports;

    @PrePersist
    private void generateUniqueToken() {
        if (token == null) {
            token = String.valueOf(UUID.randomUUID());
        }
    }
    public void addSupport(Support support){
        if (supports == null) {
            supports = new ArrayList<>();
        }
        supports.add(support);
    }
    public void addReviews(Reviews review) {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        reviews.add(review);
    }
}
