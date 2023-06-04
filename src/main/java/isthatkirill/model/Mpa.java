package isthatkirill.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Mpa {
    Integer id;
    String name;

    public Mpa(Integer id) {
        this.id = id;
    }
}
