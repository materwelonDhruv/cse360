package tests.database.tests;

import database.model.entities.Review;
import database.model.entities.User;
import database.repository.repos.Reviews;
import database.repository.repos.Users;
import org.junit.jupiter.api.*;
import tests.database.BaseDatabaseTest;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewsTest extends BaseDatabaseTest {

    private static Reviews reviewsRepo;
    private static Users usersRepo;

    @BeforeAll
    public static void setup() {
        usersRepo = appContext.users();
        reviewsRepo = appContext.reviews();

        User userA = new User("alice123", "Alice", "Doe", "pw1", "alice@example.com", RolesUtil.rolesToInt(new Roles[]{Roles.REVIEWER}));
        User userB = new User("bob456", "Bob", "Smith", "pw2", "bob@example.com", 0);
        usersRepo.create(userA);
        usersRepo.create(userB);
    }

    @Test
    @Order(1)
    public void testCreateReview() {
        User alice = usersRepo.getByUsername("alice123");
        User bob = usersRepo.getByUsername("bob456");

        Review review = new Review(alice, bob, 3);
        Review created = reviewsRepo.create(review);

        assertNotNull(created, "Review should be created successfully.");
        assertEquals(3, created.getRating());
        assertEquals(alice.getId(), created.getReviewer().getId());
        assertEquals(bob.getId(), created.getUser().getId());
    }

    @Test
    @Order(2)
    public void testGetByCompositeKey() {
        User alice = usersRepo.getByUsername("alice123");
        User bob = usersRepo.getByUsername("bob456");

        Review fetched = reviewsRepo.getByCompositeKey(alice.getId(), bob.getId());
        assertNotNull(fetched, "Fetched review should not be null.");
        assertEquals(3, fetched.getRating());
    }

    @Test
    @Order(3)
    public void testUpdateReview() {
        User alice = usersRepo.getByUsername("alice123");
        User bob = usersRepo.getByUsername("bob456");

        Review existing = reviewsRepo.getByCompositeKey(alice.getId(), bob.getId());
        existing.setRating(5);
        Review updated = reviewsRepo.update(existing);

        assertNotNull(updated, "Review after update shouldn't be null.");
        assertEquals(5, updated.getRating());
    }

    @Test
    @Order(4)
    public void testSetRating() {
        User alice = usersRepo.getByUsername("alice123");
        User bob = usersRepo.getByUsername("bob456");

        Review updated = reviewsRepo.setRating(alice, bob, 2);
        assertEquals(2, updated.getRating());
    }

    @Test
    @Order(5)
    public void testGetAllReviews() {
        List<Review> allReviews = reviewsRepo.getAll();
        assertFalse(allReviews.isEmpty(), "Expected at least one review in the DB by now.");
    }

    @Test
    @Order(6)
    public void testGetReviewersByUserIdSorted() {
        User bob = usersRepo.getByUsername("bob456");
        User charlie = new User("charlie789", "Charlie", "Brown", "pw3", "charlie@example.com", RolesUtil.rolesToInt(new Roles[]{Roles.REVIEWER}));
        User dave = new User("dave012", "Dave", "Jones", "pw4", "dave@example.com", RolesUtil.rolesToInt(new Roles[]{Roles.REVIEWER}));
        User eve = new User("eve345", "Eve", "Adams", "pw5", "eve@example.com", RolesUtil.rolesToInt(new Roles[]{Roles.REVIEWER}));
        usersRepo.create(charlie);
        usersRepo.create(dave);
        usersRepo.create(eve);

        Review reviewCharlie = new Review(charlie, bob, 4);
        Review reviewDave = new Review(dave, bob, 4);
        Review reviewEve = new Review(eve, bob, 1);
        reviewsRepo.create(reviewCharlie);
        reviewsRepo.create(reviewDave);
        reviewsRepo.create(reviewEve);

        List<Review> reviewers = reviewsRepo.getReviewersByUserId(bob.getId());
        assertEquals(4, reviewers.size());
        int prevRating = reviewers.getFirst().getRating();
        for (Review r : reviewers) {
            assertTrue(r.getRating() <= prevRating);
            prevRating = r.getRating();
        }
    }

    @Test
    @Order(7)
    public void testDeleteReview() {
        User alice = usersRepo.getByUsername("alice123");
        User bob = usersRepo.getByUsername("bob456");

        reviewsRepo.delete(alice.getId(), bob.getId());
        Review shouldBeGone = reviewsRepo.getByCompositeKey(alice.getId(), bob.getId());
        assertNull(shouldBeGone, "Review should have been deleted.");
    }
}