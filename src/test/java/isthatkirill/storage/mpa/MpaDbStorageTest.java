package isthatkirill.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import isthatkirill.model.Mpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private final MpaStorage mpaStorage;

    @Test
    public void getAllMpaTest() {
        List<Mpa> mpa = mpaStorage.getAllMpa();

        assertThat(mpa)
                .satisfies(m -> {
                    assertThat(m).hasSize(5);
                    assertThat(m.stream()
                            .map(Mpa::getName)
                            .collect(Collectors.toList()))
                            .containsExactly("G", "PG", "PG-13", "R", "NC-17");
                });
    }

    @Test
    public void findMpaByIdTest() {
        Optional<Mpa> mpaOptional = mpaStorage.findMpaById(1);

        assertThat(mpaOptional)
                .isPresent()
                .get()
                .satisfies(m -> {
                    assertThat(m).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(m).hasFieldOrPropertyWithValue("name", "G");
                });
    }

    @Test
    public void findNonExistentMpaTest() {
        Optional<Mpa> mpaOptional = mpaStorage.findMpaById(9000);

        assertThat(mpaOptional)
                .isNotPresent();
    }
}