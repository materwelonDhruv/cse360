package tests.database.tests;

import database.model.entities.User;
import database.repository.repos.Reviews;
import database.repository.repos.Users;
import org.junit.jupiter.api.*;
import tests.database.BaseDatabaseTest;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Unit tests for the {@link Users} repository.
 * <p>
 * This test class verifies various operations performed on the Users repository,
 * including user creation, retrieval, updating, deletion, and role-based queries.
 * </p>
 *
 * <p>
 * It also tests review-related functionality such as retrieving reviewers that have
 * not been rated by a specific user.
 * </p>
 *
 * @author Dhruv
 * @see Users
 * @see Reviews
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersTest extends BaseDatabaseTest {

    private static final int[] reviewerIds = new int[3];
    private static final int[] regularUserIds = new int[3];
    private static Users userRepo;

    /**
     * Initializes the Users repository before all tests.
     */
    @BeforeAll
    public static void setupUsers() {
        userRepo = appContext.users();
    }

    /**
     * Tests the creation of a new user.
     */
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

    /**
     * Tests fetching a user by their ID.
     */
    @Test
    @Order(2)
    public void testGetById() {
        User fetched = userRepo.getById(1);
        Assertions.assertNotNull(fetched, "Should find a user with ID=1");
        Assertions.assertEquals("testUser1", fetched.getUserName());
    }

    /**
     * Tests updating a user's information.
     */
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

    /**
     * Tests retrieving all users from the repository.
     */
    @Test
    @Order(4)
    public void testGetAllUsers() {
        List<User> all = userRepo.getAll();
        Assertions.assertFalse(all.isEmpty(), "Expected at least one user in the database");
    }

    /**
     * Tests deleting a user by their ID.
     */
    @Test
    @Order(5)
    public void testDeleteUser() {
        userRepo.delete(1);
        User deleted = userRepo.getById(1);
        Assertions.assertNull(deleted, "User #1 should be deleted");
    }

    /**
     * Tests creating a user with an empty password.
     */
    @Test
    @Order(6)
    public void testCreateUserWithEmptyPasswordDoesNotFail() {
        User invalid = new User("badUser", "Bad", "User", "", "bad@example.com", 0);
        User created = userRepo.create(invalid);
        Assertions.assertTrue(created.getId() > 0, "User ID should be generated");
    }

    /**
     * Tests retrieving all users with the REVIEWER role.
     */
    @Test
    @Order(7)
    public void testGetAllReviewers() {
        // Create 3 users with the REVIEWER role
        User reviewer1 = new User("rev1", "Reviewer", "One", "password", "rev1@example.com",
                RolesUtil.rolesToInt(new Roles[]{Roles.REVIEWER}));
        User reviewer2 = new User("rev2", "Reviewer", "Two", "password", "rev2@example.com",
                RolesUtil.rolesToInt(new Roles[]{Roles.REVIEWER}));
        User reviewer3 = new User("rev3", "Reviewer", "Three", "password", "rev3@example.com",
                RolesUtil.rolesToInt(new Roles[]{Roles.REVIEWER}));

        reviewer1 = userRepo.create(reviewer1);
        reviewer2 = userRepo.create(reviewer2);
        reviewer3 = userRepo.create(reviewer3);

        reviewerIds[0] = reviewer1.getId();
        reviewerIds[1] = reviewer2.getId();
        reviewerIds[2] = reviewer3.getId();

        // Create 3 normal users with no special role
        User user4 = new User("user4", "User", "Four", "password", "user4@example.com", 0);
        User user5 = new User("user5", "User", "Five", "password", "user5@example.com", 0);
        User user6 = new User("user6", "User", "Six", "password", "user6@example.com", 0);

        user4 = userRepo.create(user4);
        user5 = userRepo.create(user5);
        user6 = userRepo.create(user6);

        regularUserIds[0] = user4.getId();
        regularUserIds[1] = user5.getId();
        regularUserIds[2] = user6.getId();

        // Now test that getAllReviewers returns exactly these 3 REVIEWER users
        List<User> reviewers = userRepo.getAllReviewers();

        Assertions.assertEquals(3, reviewers.size(), "Should have exactly 3 reviewers");
        Assertions.assertTrue(reviewers.stream().anyMatch(u -> u.getId() == reviewerIds[0]));
        Assertions.assertTrue(reviewers.stream().anyMatch(u -> u.getId() == reviewerIds[1]));
        Assertions.assertTrue(reviewers.stream().anyMatch(u -> u.getId() == reviewerIds[2]));
    }

    /**
     * Tests retrieving reviewers that a user has not yet rated.
     */
    @Test
    @Order(8)
    public void testGetReviewersNotRatedByUser() throws SQLException {
        User rater = userRepo.getById(regularUserIds[0]);
        User firstReviewer = userRepo.getById(reviewerIds[0]);

        Reviews reviewsRepo = appContext.reviews();
        // Rate the first reviewer
        reviewsRepo.setRating(firstReviewer, rater, 5);

        // Now check which reviewers the user4 has NOT rated
        List<User> notRated = userRepo.getReviewersNotRatedByUser(rater.getId());

        // Expect that rev2 and rev3 are still un-rated, but rev1 is already rated
        Assertions.assertEquals(2, notRated.size(), "Should have 2 un-rated reviewers left");
        Assertions.assertTrue(notRated.stream().noneMatch(u -> u.getId() == firstReviewer.getId()));
        Assertions.assertTrue(notRated.stream().anyMatch(u -> u.getId() == reviewerIds[1]));
        Assertions.assertTrue(notRated.stream().anyMatch(u -> u.getId() == reviewerIds[2]));
    }
}