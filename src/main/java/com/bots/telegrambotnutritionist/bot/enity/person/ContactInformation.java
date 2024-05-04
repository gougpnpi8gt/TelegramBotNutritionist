package com.bots.telegrambotnutritionist.bot.enity.person;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "Поле не должно быть пустым")
    @Column(name = "Email")
    String email;

    @Column(name = "Phone_number")
    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{2}-\\d{2}")
    String phone;

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Person_ID",
            referencedColumnName = "id")
    Person PersonID;
}
