package com.bots.telegrambotnutritionist.bot.repository;

import com.bots.telegrambotnutritionist.bot.enity.webinars.Webinar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebinarRepository extends JpaRepository<Webinar, Integer> {
    Webinar findByName(String nameWebinar);
    Webinar findWebinarById(Integer id);
}
