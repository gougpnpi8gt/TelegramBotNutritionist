package com.bots.telegrambotnutritionist.bot.enity.support;

import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Requests")
@Entity
public class Support {

    @Id
    @Column(name = "Support_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "Time_creation")
    //@Temporal(TemporalType.DATE)
    LocalDate timeOfCreation;

    @Column(name = "Text_description")
    String description;

    @OneToOne
    @JoinColumn(name = "Person_ID",
        referencedColumnName = "ID")
    Person person;
}
