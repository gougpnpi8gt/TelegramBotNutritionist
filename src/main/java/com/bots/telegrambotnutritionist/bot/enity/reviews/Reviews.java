package com.bots.telegrambotnutritionist.bot.enity.reviews;

import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Reviews")
public class Reviews {

    @Id
    @OneToOne
    @JoinColumn(name = "Person_ID")
    Person person;

    @Column(name = "path_picture")
    String pictureInfo;
}
