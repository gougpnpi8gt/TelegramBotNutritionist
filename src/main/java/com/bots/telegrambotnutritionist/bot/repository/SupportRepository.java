package com.bots.telegrambotnutritionist.bot.repository;

import com.bots.telegrambotnutritionist.bot.enity.person.Person;
import com.bots.telegrambotnutritionist.bot.enity.support.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportRepository extends JpaRepository<Support, Integer> {
     List<Support> findAllByPerson(Person person);
}
