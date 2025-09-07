package database.tests;

import database.BaseDatabaseTest;
import database.model.entities.Announcement;
import database.model.entities.Message;
import database.model.entities.User;
import database.repository.repos.Announcements;
import database.repository.repos.Users;
import org.junit.jupiter.api.*;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Announcements} repository.
 * <p>
 * This test class verifies creation, retrieval, updating, and deletion
 * of announcements and ensures that the underlying messages are linked correctly.
 * </p>
 *
 * @author Dhruv
 * @see Announcements
 * @see Users
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AnnouncementsTest extends BaseDatabaseTest {

    private static Announcements announcementsRepo;
    private static Users usersRepo;
    private static int staffUserId;

    @BeforeAll
    public static void setup() {
        usersRepo = appContext.users();
        announcementsRepo = appContext.announcements();

        // Create or fetch a staff user
        User staff = new User("annStaff", "Staff", "ForAnnouncements", "pw", "annstaff@example.com", RolesUtil.rolesToInt(new Roles[]{Roles.STAFF}));
        usersRepo.create(staff);
        staffUserId = staff.getId();
    }

    @Test
    @Order(1)
    public void testCreateAnnouncement() {
        Message msg = new Message(staffUserId, "Announcement content #1");
        Announcement ann = new Announcement(msg, "Announcement Title #1");
        Announcement created = announcementsRepo.create(ann);

        assertNotNull(created.getId(), "Announcement ID should be set after creation");
        assertEquals("Announcement Title #1", created.getTitle());
        assertEquals("Announcement content #1", created.getMessage().getContent());
        assertEquals(staffUserId, created.getMessage().getUserId());
    }

    @Test
    @Order(2)
    public void testGetById() {
        Announcement ann = announcementsRepo.getById(1);
        assertNotNull(ann, "Announcement with ID=1 should exist");
        assertEquals("Announcement Title #1", ann.getTitle());
    }

    @Test
    @Order(3)
    public void testUpdateAnnouncement() {
        Announcement ann = announcementsRepo.getById(1);
        ann.setTitle("Updated Title");
        ann.getMessage().setContent("Updated announcement content");
        Announcement updated = announcementsRepo.update(ann);

        assertNotNull(updated);
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated announcement content", updated.getMessage().getContent());
    }

    @Test
    @Order(4)
    public void testGetAll() {
        // Create a second announcement
        Message msg2 = new Message(staffUserId, "Second Announcement Content");
        Announcement ann2 = new Announcement(msg2, "Second Title");
        announcementsRepo.create(ann2);

        List<Announcement> all = announcementsRepo.getAll();
        assertTrue(all.size() >= 2, "Should have at least two announcements in total");
    }

    @Test
    @Order(5)
    public void testDeleteAnnouncement() {
        announcementsRepo.delete(1);
        Announcement shouldBeGone = announcementsRepo.getById(1);
        assertNull(shouldBeGone, "Announcement #1 should have been deleted");
    }
}