package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFeed {
    Long eventId;
    Long userId;
    Long entityId;
    EventType eventType;
    Operation operation;
    Long timestamp;
}
