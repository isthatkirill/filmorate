package isthatkirill.storage;

import isthatkirill.model.User;

import java.util.List;

public interface FriendshipStorage {
    Long addFriend(Long userId, Long friendId);

    List<User> getFriendsByUserId(Long userId);

    Long deleteFriend(Long userId, Long friendId);

    Boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    List<User> getCommonFriends(Long firstUserId, Long secondUserId);
}
