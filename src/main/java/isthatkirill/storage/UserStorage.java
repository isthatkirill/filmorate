package isthatkirill.storage;

import isthatkirill.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    Optional<User> findUserById(long id);

    void deleteUser(Long id);
}
