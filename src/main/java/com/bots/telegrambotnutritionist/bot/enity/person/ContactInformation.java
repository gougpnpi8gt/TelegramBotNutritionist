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

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Email(message = "Сообщение должно иметь формат адреса электронной почты")
    @Column(name = "email")
    String email;

    @Column(name = "phone_number")
    @Pattern(regexp = "^\\+7\\(\\d{3}\\)-\\d{3}-\\d{2}-\\d{2}$", message = "Не верный формат номера телефона")
    String phone;

    @OneToOne
    @JoinColumn(name = "person_id",
            referencedColumnName = "id")
    Person person;
}
