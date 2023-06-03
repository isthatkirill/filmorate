package isthatkirill.storage;

import isthatkirill.model.UserFeed;

import java.util.List;

public interface UserFeedStorage {
    UserFeed save(UserFeed userFeed);

    List<UserFeed> getAllByUserId(Long id);
}
