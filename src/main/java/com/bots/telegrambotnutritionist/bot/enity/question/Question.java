package com.bots.telegrambotnutritionist.bot.enity.question;

import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Questions")
public class Question {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "description")
    String description;

    @ManyToOne
    @JoinColumn(name = "person_id",
            referencedColumnName = "id")
    Person person;

    @Column(name = "answer_admin")
    String answerAdmin;

    public Question(String description, Person person) {
        this.description = description;
        this.person = person;
    }

    public Question() {

    }
}
