package tests.database.tests;

import database.model.entities.ReviewerRequest;
import database.model.entities.User;
import database.repository.repos.ReviewerRequests;
import database.repository.repos.Users;
import org.junit.jupiter.api.*;
import tests.database.BaseDatabaseTest;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewerRequestsTest extends BaseDatabaseTest {

    private static ReviewerRequests rrRepo;
    private static Users usersRepo;
    private static int requestId1;

    @BeforeAll
    public static void setup() {
        usersRepo = appContext.users();
        rrRepo = appContext.reviewerRequests();

        User u1 = new User("reqUser1", "Requester", "One", "pw1", "req1@example.com", 0);
        User i1 = new User("instrUser1", "Instructor", "One", "pw2", "instr1@example.com", RolesUtil.rolesToInt(new Roles[]{Roles.INSTRUCTOR}));
        usersRepo.create(u1);
        usersRepo.create(i1);

        ReviewerRequest rr = new ReviewerRequest(u1, i1, null);
        ReviewerRequest created = rrRepo.create(rr);
        requestId1 = created.getId();
    }

    @Test
    @Order(1)
    public void testCreateReviewerRequest() {
        ReviewerRequest fetched = rrRepo.getById(requestId1);
        assertNotNull(fetched);
        assertNull(fetched.getStatus());
    }

    @Test
    @Order(2)
    public void testUpdateReviewerRequest() {
        ReviewerRequest rr = rrRepo.getById(requestId1);
        User u2 = new User("instrUser2", "Instructor", "Two", "pw3", "instr2@example.com", RolesUtil.rolesToInt(new Roles[]{Roles.INSTRUCTOR}));
        usersRepo.create(u2);
        rr.setInstructor(u2);
        rr.setStatus(true);
        ReviewerRequest updated = rrRepo.update(rr);
        assertNotNull(updated);
        assertTrue(updated.getStatus());
        assertEquals(u2.getId(), updated.getInstructor().getId());
    }

    @Test
    @Order(3)
    public void testAcceptRequest() {
        ReviewerRequest accepted = rrRepo.acceptRequest(requestId1);
        assertNotNull(accepted);
        assertTrue(accepted.getStatus());
    }

    @Test
    @Order(4)
    public void testRejectRequest() {
        ReviewerRequest rr = rrRepo.getById(requestId1);
        rr.setStatus(null);
        rrRepo.update(rr);
        ReviewerRequest rejected = rrRepo.rejectRequest(requestId1);
        assertNotNull(rejected);
        assertFalse(rejected.getStatus());
    }

    @Test
    @Order(5)
    public void testGetAll() {
        List<ReviewerRequest> all = rrRepo.getAll();
        assertFalse(all.isEmpty());
    }

    @Test
    @Order(6)
    public void testGetRequestsByUser() {
        User u = usersRepo.getByUsername("reqUser1");
        List<ReviewerRequest> userRequests = rrRepo.getRequestsByUser(u.getId());
        assertFalse(userRequests.isEmpty());
        for (ReviewerRequest r : userRequests) {
            assertEquals(u.getId(), r.getRequester().getId());
        }
    }

    @Test
    @Order(7)
    public void testGetRequestsByInstructor() {
        User i2 = usersRepo.getByUsername("instrUser2");
        List<ReviewerRequest> instRequests = rrRepo.getRequestsByInstructor(i2.getId());
        assertFalse(instRequests.isEmpty());
        for (ReviewerRequest r : instRequests) {
            assertEquals(i2.getId(), r.getInstructor().getId());
        }
    }

    @Test
    @Order(8)
    public void testDelete() {
        rrRepo.delete(requestId1);
        ReviewerRequest gone = rrRepo.getById(requestId1);
        assertNull(gone);
    }
}