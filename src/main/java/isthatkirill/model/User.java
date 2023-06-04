package isthatkirill.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Positive
    Long id;
    @Email
    @NotBlank
    String email;
    @NotBlank
    @Pattern(regexp = "^[^ ]+$")
    String login;
    String name;
    @NotNull
    @PastOrPresent
    LocalDate birthday;
}
