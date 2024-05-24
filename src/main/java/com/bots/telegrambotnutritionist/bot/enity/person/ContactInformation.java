package com.bots.telegrambotnutritionist.bot.enity.person;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Contacts")
public class ContactInformation {

    @Email
   // @NotEmpty(message = "Поле не должно быть пустым")
    @Column(name = "email")
    String email;

    @Column(name = "phone_number")
    //@Pattern(regexp = "\\d{3}-\\d{3}-\\d{2}-\\d{2}")
    String phone;

    @Id
    @Column(name = "id")
    Integer id;

    @OneToOne
    @JoinColumn(name = "person_id",
            referencedColumnName = "id")
    Person PersonID;
}
