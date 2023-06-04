package isthatkirill.service;

import isthatkirill.annotation.SaveUserFeed;
import isthatkirill.exceptions.EntityNotFoundException;
import isthatkirill.exceptions.OnUpdateException;
import isthatkirill.model.User;
import isthatkirill.model.UserFeed;
import isthatkirill.model.enums.EventType;
import isthatkirill.model.enums.Operation;
import isthatkirill.storage.FriendshipStorage;
import isthatkirill.storage.UserFeedStorage;
import isthatkirill.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserFeedStorage userFeedStorage;
    private final FriendshipStorage friendshipStorage;

    @SaveUserFeed(
            value = EventType.FRIEND,
            operation = Operation.ADD,
            userIdPropertyName = "userId",
            entityIdPropertyName = "friendId"
    )
    public User addFriend(Long userId, Long friendId) {
        User user = checkUserExistent(userId);
        checkUserExistent(friendId);
        if (existsByUserIdAndFriendId(userId, friendId)) {
            throw new OnUpdateException("This user is already in your list");
        }
        friendshipStorage.addFriend(userId, friendId);
        log.info("User {} added user {} to the friend list", userId, friendId);
        return user;
    }

    @SaveUserFeed(
            value = EventType.FRIEND,
            operation = Operation.REMOVE,
            userIdPropertyName = "userId",
            entityIdPropertyName = "friendId"
    )
    public User deleteFriend(Long userId, Long friendId) {
        User user = checkUserExistent(userId);
        checkUserExistent(friendId);
        if (!existsByUserIdAndFriendId(userId, friendId)) {
            throw new OnUpdateException("This user not is your friend");
        }
        friendshipStorage.deleteFriend(userId, friendId);
        log.info("User {} removed user {} from friend list", userId, friendId);
        return user;
    }

    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        checkUserExistent(firstUserId);
        checkUserExistent(secondUserId);
        log.info("User with id {} requested a list of mutual friends with user {}", firstUserId, secondUserId);
        return friendshipStorage.getCommonFriends(firstUserId, secondUserId);
    }

    public List<User> getAllFriends(Long userId) {
        checkUserExistent(userId);
        return getFriendsByUserId(userId);
    }

    public User addUser(User user) {
        updateUserIfNameIsBlank(user);
        userStorage.addUser(user);
        log.info("User added: {}", user.getLogin());
        return user;
    }

    public User updateUser(User user) {
        updateUserIfNameIsBlank(user);
        checkUserExistent(user.getId());
        return userStorage.updateUser(user);
    }

    public void deleteUser(Long id) {
        checkUserExistent(id);
        log.info("User deleted: {}", id);
        userStorage.deleteUser(id);
    }

    public User getUserById(Long id) {
        log.info("Get user by id = {}", id);
        return checkUserExistent(id);
    }

    public List<User> getAllUsers() {
        List<User> users = userStorage.getAllUsers();
        log.info("Number of users: {}", users.size());
        return users;
    }

    public User checkUserExistent(Long id) {
        return userStorage
                .findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, "Id: " + id));
    }

    public Boolean existsByUserIdAndFriendId(Long userId, Long friendId) {
        return friendshipStorage.existsByUserIdAndFriendId(userId, friendId);
    }

    public List<UserFeed> getFeed(Long userId) {
        checkUserExistent(userId);
        return userFeedStorage.getAllByUserId(userId);
    }

    private void updateUserIfNameIsBlank(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("For the user {} login used as the name.", user.getLogin());
        }
    }

    private List<User> getFriendsByUserId(Long userId) {
        return friendshipStorage.getFriendsByUserId(userId);
    }
}
