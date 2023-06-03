package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SaveUserFeed {
    EventType value();
    Operation operation();
    String userIdPropertyName();
    String entityIdPropertyName();
}


