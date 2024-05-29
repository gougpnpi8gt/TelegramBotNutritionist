package com.bots.telegrambotnutritionist.bot.enity.person;

public enum Action {
    FREE,
    ADMIN,
    /*
    Для отправления заявки
     */
    DATE,
    DATE_NAME,
    DATE_SURNAME,
    DATE_PATRONYMIC,
    DATE_AGE,
    DATE_GENDER,
    DATE_WEIGHT,
    DATE_BIRTHDAY,
    DATE_COUNTRY,
    DATE_CHARACTERISTIC,
    DATE_EMAIL,
    DATE_PHONE,
    DATE_SUPPORT,
    SUBMIT_DESCRIPTION,
    /*
    Для отправки вопросов и отзывов
     */
    SENDING_QUESTION,
    SENDING_REVIEW,
    SENDING_ADMIN,
    /*
    Управление командами
     */
    ADD_COMMAND,
    DELETE_COMMAND,
    UPDATE_COMMAND,
    AUTH
}
