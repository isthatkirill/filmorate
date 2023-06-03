package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Map<Long, User> getAllUsersMap() {
        return users;
    }

    @Override
    public List<User> getFriendsByTheirIds(List<Long> friendsIds) {
        List<User> friends = new ArrayList<>();
        for (Long id : friendsIds) {
            if (users.containsKey(id)) {
                friends.add(users.get(id));
            }
        }
        return friends;
    }

}
