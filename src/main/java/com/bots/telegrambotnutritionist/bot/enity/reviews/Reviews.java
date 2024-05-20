package com.bots.telegrambotnutritionist.bot.enity.reviews;

import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Reviews")
public class Reviews {

    @Id
    @Column(name = "id")
    Integer id;

    @OneToOne
    @JoinColumn(name = "person_id",
                referencedColumnName = "id")
    Person person;

    @Column(name = "description")
    String description;

    public Reviews(Person person, String text) {
        this.person = person;
        this.description = text;
    }
}