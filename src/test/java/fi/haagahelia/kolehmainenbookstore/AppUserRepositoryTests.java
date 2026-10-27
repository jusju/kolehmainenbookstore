package fi.haagahelia.kolehmainenbookstore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import fi.haagahelia.kolehmainenbookstore.domain.AppUser;
import fi.haagahelia.kolehmainenbookstore.domain.AppUserRepository;

@DataJpaTest
public class AppUserRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser user1;
    private AppUser user2;

    @BeforeEach
    void setUp() {
        user1 = new AppUser(
                "testuser1", "test-hash-1",
                "testuser1@example.com", "ROLE_USER");

        user2 = new AppUser(
                "testuser2", "test-hash-2",
                "testuser2@example.com", "ROLE_ADMIN");
    }

    @Test
    public void saveNewUser() {
        appUserRepository.save(user1);
        assertThat(user1.getId()).isNotNull();

        Long userId = user1.getId();

        entityManager.flush();
        entityManager.clear();

        AppUser savedUser =
                appUserRepository.findById(userId).orElseThrow();

        assertThat(savedUser.getUsername()).isEqualTo("testuser1");
        assertThat(savedUser.getPasswordHash()).isEqualTo("test-hash-1");
        assertThat(savedUser.getEmail()).isEqualTo("testuser1@example.com");
        assertThat(savedUser.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    public void findByUsernameReturnsMatchingUser() {
        appUserRepository.save(user1);
        appUserRepository.save(user2);

        Long userId = user1.getId();

        entityManager.flush();
        entityManager.clear();

        AppUser foundUser = appUserRepository.findByUsername("testuser1");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getEmail()).isEqualTo("testuser1@example.com");
    }

    @Test
    public void findByEmailReturnsMatchingUser() {
        appUserRepository.save(user1);
        appUserRepository.save(user2);

        Long userId = user2.getId();

        entityManager.flush();
        entityManager.clear();

        AppUser foundUser =
                appUserRepository.findByEmail("testuser2@example.com");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getUsername()).isEqualTo("testuser2");
    }

    @Test
    public void updatingUserChangesEmailAndKeepsId() {
        appUserRepository.save(user1);
        Long userId = user1.getId();

        user1.setEmail("updated@example.com");

        entityManager.flush();
        entityManager.clear();

        AppUser updatedUser =
                appUserRepository.findById(userId).orElseThrow();

        assertThat(updatedUser.getId()).isEqualTo(userId);
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    public void deletedUserCannotBeFoundById() {
        appUserRepository.save(user1);
        Long userId = user1.getId();

        appUserRepository.delete(user1);

        entityManager.flush();
        entityManager.clear();

        assertThat(appUserRepository.findById(userId)).isEmpty();
    }
}