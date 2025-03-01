package tests.database.tests;

import org.junit.jupiter.api.*;
import database.model.entities.User;
import database.repository.repos.Users;
import tests.database.BaseDatabaseTest;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersTest extends BaseDatabaseTest {

    private static Users userRepo;

    @BeforeAll
    public static void setupUsers() {
        userRepo = appContext.users();
    }

    @Test
    @Order(1)
    public void testCreateUser() {
        User u = new User(
                "testUser1",
                "Test",
                "User",
                "SomePassword",
                "testuser1@example.com",
                0  // roles int
        );

        User created = userRepo.create(u);
        Assertions.assertTrue(created.getId() > 0, "User ID should be generated");
        Assertions.assertEquals("testUser1", created.getUserName());
    }

    @Test
    @Order(2)
    public void testGetById() {
        User fetched = userRepo.getById(1);
        Assertions.assertNotNull(fetched, "Should find a user with ID=1");
        Assertions.assertEquals("testUser1", fetched.getUserName());
    }

    @Test
    @Order(3)
    public void testUpdateUser() {
        User existing = userRepo.getById(1);
        existing.setFirstName("UpdatedFirst");
        existing.setLastName("UpdatedLast");
        existing.setPassword("NewPassword");

        User updated = userRepo.update(existing);
        Assertions.assertEquals("UpdatedFirst", updated.getFirstName());
        Assertions.assertEquals("UpdatedLast", updated.getLastName());
    }

    @Test
    @Order(4)
    public void testGetAllUsers() {
        List<User> all = userRepo.getAll();
        Assertions.assertFalse(all.isEmpty(), "Expected at least one user in the database");
    }

    @Test
    @Order(5)
    public void testDeleteUser() {
        userRepo.delete(1);
        User deleted = userRepo.getById(1);
        Assertions.assertNull(deleted, "User #1 should be deleted");
    }

    @Test
    @Order(6)
    public void testCreateUserWithEmptyPasswordDoesNotFail() {
        User invalid = new User("badUser", "Bad", "User", "", "bad@example.com", 0);
        User created = userRepo.create(invalid);
        Assertions.assertTrue(created.getId() > 0, "User ID should be generated");
    }
}
