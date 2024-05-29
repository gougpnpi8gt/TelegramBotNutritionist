package com.bots.telegrambotnutritionist.bot.repository;

import com.bots.telegrambotnutritionist.bot.enity.person.ContactInformation;
import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactsRepository extends JpaRepository<ContactInformation, Integer> {
    ContactInformation findContactInformationByPerson(Person person);
}
