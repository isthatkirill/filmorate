package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.OnUpdateException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private Long id = 0L;

    public User addFriend(Long userId, Long friendId) {
        User user = checkUserExistent(userId);
        User friend = checkUserExistent(friendId);
        if (user.getFriends().contains(friendId)) {
            throw new OnUpdateException("Users are already friends");
        } else {
            user.addFriend(friendId);
            friend.addFriend(userId);
            log.info("User " + userId + " added user " + friendId + " to the friend list");
            return friend;
        }
    }

    public User deleteFriend(Long userId, Long friendId) {
        User user = checkUserExistent(userId);
        User friend = checkUserExistent(friendId);
        if (!user.deleteFriend(friendId)) {
            throw new UserNotFoundException("This user not is your friend");
        } else {
            friend.deleteFriend(userId);
            log.info("User " + userId + " removed user " + friendId + " from friend list");
            return friend;
        }
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        User user = checkUserExistent(userId);
        User friend = checkUserExistent(friendId);

        log.info("User with id " + userId + "requested a list of mutual friends with user " + friendId);
        List<Long> usersFriends = user.getFriends();
        List<Long> friendFriends = friend.getFriends();

        return getFriendsByTheirIds(usersFriends.stream()
                .filter(friendFriends::contains)
                .collect(Collectors.toList()));
    }


    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("For the user " + user.getLogin() + " login used as the name.");
        }
        user.setId(++id);
        userStorage.addUser(user);
        log.info("User added: " + user.getLogin());
        return user;
    }

    public User updateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("For the user " + user.getLogin() + " login used as the name.");
        }
        checkUserExistent(user.getId());
        userStorage.updateUser(user);
        return user;
    }

    public User getUser(Long id) {
        return checkUserExistent(id);
    }

    public List<User> getAllFriends(Long id) {
        User user = checkUserExistent(id);
        return getFriendsByTheirIds(user.getFriends());
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Map<Long, User> getAllUsersMap() {
        return userStorage.getAllUsersMap();
    }

    public User checkUserExistent(Long id) {
        return userStorage
                .findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    private List<User> getFriendsByTheirIds(List<Long> usersId) {
        return userStorage.getFriendsByTheirIds(usersId);
    }
}
