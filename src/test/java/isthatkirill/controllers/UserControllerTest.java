package isthatkirill.controllers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import isthatkirill.exceptions.UserNotFoundException;
import isthatkirill.model.User;
import isthatkirill.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private UserStorage userStorage;
    private UserService userService;
    private UserController userController;
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void create() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void destroy() {
        validatorFactory.close();
    }

    @BeforeEach
    public void beforeEach() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @Test
    public void validUserTest() {
        User user = User.builder()
                .name("Test name")
                .email("test.email@yandex.ru")
                .login("TestLogin222")
                .birthday(LocalDate.of(2002, 2, 2))
                .build();

        userController.addUser(user);
        var validates = validator.validate(user);

        assertEquals(0, validates.size());
        assertEquals(user, userController.getAllUsers().get(0));
        assertEquals(1, userController.getAllUsers().get(0).getId());
    }

    @Test
    public void invalidEmailTest() {
        User user = User.builder()
                .name("Test name")
                .email("google.com")
                .login("TestLogin222")
                .birthday(LocalDate.of(2002, 2, 2))
                .build();

        userController.addUser(user);
        var validates = validator.validate(user);

        assertEquals(1, validates.size());
        validationInfo(validates);
    }

    @Test
    public void invalidLoginTest() {
        User user = User.builder()
                .name("Test name")
                .email("test.email@yandex.ru")
                .login("")
                .birthday(LocalDate.of(2002, 2, 2))
                .build();

        userController.addUser(user);
        var validates = validator.validate(user);

        assertEquals(1, validates.size());
        validationInfo(validates);
    }

    @Test
    public void emptyNameTest() {
        User user = User.builder()
                .email("test.email@yandex.ru")
                .login("TestLogin222")
                .birthday(LocalDate.of(2002, 2, 2))
                .build();
        userController.addUser(user);

        assertEquals(userController.getAllUsers().get(0).getLogin(), userController.getAllUsers().get(0).getName());
    }

    @Test
    public void invalidBirthDayTest() {
        User user = User.builder()
                .name("Test name")
                .email("test.email@yandex.ru")
                .login("TestLogin222")
                .birthday(LocalDate.of(2222, 2, 2))
                .build();
        userController.addUser(user);

        var validates = validator.validate(user);

        assertEquals(1, validates.size());
        validationInfo(validates);
    }

    @Test
    public void updateValidUserTest() {
        User user = User.builder()
                .name("Test name")
                .email("test.email@yandex.ru")
                .login("TestLogin222")
                .birthday(LocalDate.of(2222, 2, 2))
                .build();
        userController.addUser(user);

        User updated = User.builder()
                .name("New user for test")
                .email("test.email@yandex.ru")
                .login("TestLogin222")
                .id(user.getId())
                .birthday(LocalDate.of(2222, 2, 2))
                .build();
        userController.updateUser(updated);

        assertEquals(updated, userController.getAllUsers().get(0));
    }

    @Test
    public void updateNonExistentTest() {
        User user = User.builder()
                .name("Test name")
                .email("test.email@yandex.ru")
                .login("TestLogin222")
                .id(5L)
                .birthday(LocalDate.of(2222, 2, 2))
                .build();

        Exception e = assertThrows(UserNotFoundException.class, () -> {
            userController.updateUser(user);
        });

        assertEquals("User with id 5 not found", e.getMessage());
    }

    private void validationInfo(Set<ConstraintViolation<User>> validates) {
        validates.stream()
                .map(ConstraintViolation::getMessage)
                .forEach(System.out::println);
    }

}