package isthatkirill.model;

import isthatkirill.model.enums.Operation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import isthatkirill.model.enums.EventType;

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
