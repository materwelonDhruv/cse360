package tests.database.tests;

import database.model.entities.AdminRequest;
import database.model.entities.User;
import database.repository.repos.AdminRequests;
import database.repository.repos.Users;
import org.junit.jupiter.api.*;
import tests.database.BaseDatabaseTest;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;
import utils.requests.AdminActions;
import utils.requests.RequestState;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AdminRequests} repository.
 *
 * <p> Covers: create, read, update, delete, state-only update, filter queries,
 * and validator edge-cases for all three {@link AdminActions} types. </p>
 *
 * @author Dhruv
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminRequestsTest extends BaseDatabaseTest {

    private static AdminRequests arRepo;
    private static Users usersRepo;
    private static int delReqId, updReqId, passReqId;
    private static int instructorId, targetAId, targetBId;

    /**
     * Prepare users and three requests (DeleteUser, UpdateRole, RequestPassword).
     */
    @BeforeAll
    public static void setup() {
        usersRepo = appContext.users();
        arRepo = appContext.adminRequests();

        // Instructor (requester) – must have INSTRUCTOR role
        User instructor = new User(
                "inst1", "Inst", "One", "pw-i",
                "inst@example.com",
                RolesUtil.rolesToInt(new Roles[]{Roles.INSTRUCTOR})
        );
        usersRepo.create(instructor);
        instructorId = instructor.getId();

        // Two normal users (targets)
        User targetA = new User("tuserA", "Tar", "A", "pwA", "ta@example.com", 0);
        User targetB = new User("tuserB", "Tar", "B", "pwB", "tb@example.com", 0);
        usersRepo.create(targetA);
        usersRepo.create(targetB);
        targetAId = targetA.getId();
        targetBId = targetB.getId();

        // 1) DeleteUser request
        AdminRequest del = new AdminRequest(
                instructor, targetA,
                AdminActions.DeleteUser,
                RequestState.Pending,
                "Violation of rules",
                null
        );
        delReqId = arRepo.create(del).getId();

        // 2) UpdateRole request (context = REVIEWER bit)
        int reviewerBit = Roles.REVIEWER.getBit();
        AdminRequest upd = new AdminRequest(
                instructor, targetA,
                AdminActions.UpdateRole,
                RequestState.Pending,
                "Promote to reviewer",
                reviewerBit
        );
        updReqId = arRepo.create(upd).getId();

        // 3) RequestPassword reset
        AdminRequest pass = new AdminRequest(
                instructor, targetB,
                AdminActions.RequestPassword,
                RequestState.Pending,
                "Forgot password",
                null
        );
        passReqId = arRepo.create(pass).getId();
    }

    @Test
    @Order(1)
    public void testCreateAndGetById() throws SQLException {
        AdminRequest fetched = arRepo.getById(delReqId);
        assertNotNull(fetched);
        assertEquals(AdminActions.DeleteUser, fetched.getType());
        assertEquals(RequestState.Pending, fetched.getState());
    }

    @Test
    @Order(2)
    public void testGetAll() throws SQLException {
        List<AdminRequest> all = arRepo.getAll();
        assertTrue(all.size() >= 3);
    }

    @Test
    @Order(3)
    public void testFilterFetchByActionAndState() {
        List<AdminRequest> deletes = arRepo.filterFetch(AdminActions.DeleteUser, RequestState.Pending);
        assertTrue(deletes.stream().anyMatch(r -> r.getId() == delReqId));
    }

    @Test
    @Order(4)
    public void testFilterFetchByActionStateRequester() {
        List<AdminRequest> requesterUpdates =
                arRepo.filterFetch(AdminActions.UpdateRole, RequestState.Pending, instructorId);
        assertEquals(1, requesterUpdates.size());
        assertEquals(updReqId, requesterUpdates.getFirst().getId());
    }

    @Test
    @Order(5)
    public void testSetState() {
        AdminRequest accepted = arRepo.setState(delReqId, RequestState.Accepted);
        assertNotNull(accepted);
        assertEquals(RequestState.Accepted, accepted.getState());

        AdminRequest rejected = arRepo.setState(passReqId, RequestState.Denied);
        assertEquals(RequestState.Denied, rejected.getState());
    }

    @Test
    @Order(6)
    public void testUpdateWholeRequest() throws SQLException {
        AdminRequest upd = arRepo.getById(updReqId);
        upd.setReason("Promote to reviewer – verified");
        upd.setContext(Roles.REVIEWER.getBit()); // keep same context
        upd.setState(RequestState.Accepted);
        AdminRequest saved = arRepo.update(upd);
        assertEquals("Promote to reviewer – verified", saved.getReason());
        assertEquals(RequestState.Accepted, saved.getState());
    }

    @Test
    @Order(7)
    public void testValidatorEdgeCase_ContextRequiredForUpdateRole() {
        AdminRequest bad = new AdminRequest(
                usersRepo.getById(instructorId),
                usersRepo.getById(targetBId),
                AdminActions.UpdateRole,
                RequestState.Pending,
                "Missing context",
                null   // context absent -> should fail validation
        );
        assertThrows(IllegalArgumentException.class, () -> arRepo.create(bad));
    }

    @Test
    @Order(8)
    public void testDelete() throws SQLException {
        arRepo.delete(passReqId);
        assertNull(arRepo.getById(passReqId));
    }

    @Test
    @Order(9)
    public void testSetStateNonExistent() {
        AdminRequest none = arRepo.setState(999999, RequestState.Accepted);
        assertNull(none);
    }
}