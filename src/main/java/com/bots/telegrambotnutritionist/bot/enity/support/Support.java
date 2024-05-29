package com.bots.telegrambotnutritionist.bot.enity.support;

import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Supports")
@Entity
public class Support {

    @Id
    @Column(name = "support_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "time_creation")
    @Temporal(TemporalType.DATE)
    LocalDate timeOfCreation;

    @Column(name = "text_description")
    String description;

    @ManyToOne
    @JoinColumn(name = "person_id",
        referencedColumnName = "id")
    Person person;

    @Override
    public String toString() {
        return "\uD83E\uDD86 Заявка: " + "\n" +
                "Номер заявки - " + id + "\n" +
                "Время создания - " + timeOfCreation + "\n" +
                "Описание: " + description + "\n";
    }
}
