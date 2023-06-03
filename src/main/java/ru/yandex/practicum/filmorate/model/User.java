package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class User {
    @Positive
    private Long id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();

    public void addFriend(@Positive Long id) {
        friends.add(id);
    }

    public boolean deleteFriend(@Positive Long id) {
        return friends.remove(id);
    }

    public List<Long> getFriends() {
        return new ArrayList<>(friends);
    }

}
