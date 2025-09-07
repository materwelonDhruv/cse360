package database.tests;

import database.BaseDatabaseTest;
import database.model.entities.Message;
import database.model.entities.StaffMessage;
import database.model.entities.User;
import database.repository.repos.StaffMessages;
import database.repository.repos.Users;
import org.junit.jupiter.api.*;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link StaffMessages} repository.
 * <p>
 * This test class verifies the functionality of operations performed on the "StaffMessages" table,
 * including sending messages, loading chat histories, retrieving unique chat partners, and deletion.
 * It covers various scenarios with multiple users and staff members.
 * </p>
 *
 * @see StaffMessages
 * @see Users
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StaffMessagesTest extends BaseDatabaseTest {

    private static StaffMessages staffMessagesRepo;
    private static Users usersRepo;
    private static int user1Id, user2Id, staff1Id, staff2Id;

    /**
     * Initializes repositories and creates test users.
     */
    @BeforeAll
    public static void setup() {
        usersRepo = appContext.users();
        staffMessagesRepo = appContext.staffMessages();

        // Create non-staff users.
        User user1 = new User("user1", "User", "One", "password", "user1@example.com", 0);
        User user2 = new User("user2", "User", "Two", "password", "user2@example.com", 0);
        usersRepo.create(user1);
        usersRepo.create(user2);
        user1Id = user1.getId();
        user2Id = user2.getId();

        // Create staff users.
        User staff1 = new User("staff1", "Staff", "One", "password", "staff1@example.com", RolesUtil.rolesToInt(new Roles[]{Roles.STAFF}));
        usersRepo.create(staff1);
        staff1Id = staff1.getId();

        User staff2 = new User("staff2", "Staff", "Two", "password", "staff2@example.com", RolesUtil.rolesToInt(new Roles[]{Roles.STAFF}));
        usersRepo.create(staff2);
        staff2Id = staff2.getId();
    }

    /**
     * Tests sending a message from a non-staff user to a staff member using the updated sendMessage method.
     */
    @Test
    @Order(1)
    public void testSendMessageUserToStaff() {
        Message msg = new Message(user1Id, "Hello staff from user1");
        StaffMessage sm = staffMessagesRepo.sendMessage(msg, user1Id, staff1Id);

        assertNotNull(sm.getId(), "Message ID should be generated");
        assertEquals("Hello staff from user1", sm.getMessage().getContent(), "Message content must match");
        assertEquals(user1Id, sm.getMessage().getUserId(), "Sender ID in the message should be user1");
        assertEquals(user1Id, sm.getUser().getId(), "The user in the staff message should be user1");
        assertEquals(staff1Id, sm.getStaff().getId(), "The staff in the staff message should be staff1");
    }

    /**
     * Tests sending a message from a staff member to a non-staff user using the updated sendMessage method.
     */
    @Test
    @Order(2)
    public void testSendMessageStaffToUser() {
        Message msg = new Message(staff1Id, "Hello user1 from staff1");
        // The sender (staff) sets the message's sender ID.
        // For staff-to-user communication, the non-staff recipient is provided via the userId parameter.
        StaffMessage sm = staffMessagesRepo.sendMessage(msg, user1Id, staff1Id);

        assertNotNull(sm.getId(), "Message ID should be generated");
        assertEquals("Hello user1 from staff1", sm.getMessage().getContent(), "Message content must match");
        assertEquals(staff1Id, sm.getMessage().getUserId(), "Sender ID in the message should be staff1");
        assertEquals(user1Id, sm.getUser().getId(), "The user in the staff message should be user1");
        assertEquals(staff1Id, sm.getStaff().getId(), "The staff in the staff message should be staff1");
    }

    /**
     * Tests loading the chat history between a specific non-staff user and a staff member.
     */
    @Test
    @Order(3)
    public void testLoadChat() {
        // Prepare a chat between user1 and staff1 with several messages.
        Message msg1 = new Message(user1Id, "Hi, this is user1's second message");
        staffMessagesRepo.sendMessage(msg1, user1Id, staff1Id);

        Message msg2 = new Message(staff1Id, "Reply from staff1");
        staffMessagesRepo.sendMessage(msg2, user1Id, staff1Id);

        Message msg3 = new Message(user1Id, "User1 again");
        staffMessagesRepo.sendMessage(msg3, user1Id, staff1Id);

        // Load chat history in chronological order.
        List<StaffMessage> chat = staffMessagesRepo.loadChat(user1Id, staff1Id);
        assertTrue(chat.size() >= 3, "The chat should contain at least 3 messages");

        // Verify chronological order based on content.
        assertEquals("Hello staff from user1", chat.get(0).getMessage().getContent(), "First message content mismatch");
        assertEquals("Hello user1 from staff1", chat.get(1).getMessage().getContent(), "Second message content mismatch");
        assertEquals("Hi, this is user1's second message", chat.get(2).getMessage().getContent(), "Third message content mismatch");
        assertEquals("Reply from staff1", chat.get(3).getMessage().getContent(), "Fourth message content mismatch");
        assertEquals("User1 again", chat.get(4).getMessage().getContent(), "Fifth message content mismatch");
    }

    /**
     * Tests retrieving unique chat partners for a given staff member.
     */
    @Test
    @Order(4)
    public void testGetUniqueChats() {
        // Create additional messages in different chat scenarios.
        // user2 sends a message to staff1.
        Message msg1 = new Message(user2Id, "Hello from user2 to staff1");
        staffMessagesRepo.sendMessage(msg1, user2Id, staff1Id);

        // user2 sends a message to staff2.
        Message msg2 = new Message(user2Id, "Hello from user2 to staff2");
        staffMessagesRepo.sendMessage(msg2, user2Id, staff2Id);

        // Retrieve unique chats for staff1.
        List<User> staff1Chats = staffMessagesRepo.getUniqueChats(staff1Id);
        // Staff1 should be chatting with both user1 and user2.
        assertTrue(staff1Chats.stream().anyMatch(u -> u.getId() == user1Id), "Staff1 should have a chat with user1");
        assertTrue(staff1Chats.stream().anyMatch(u -> u.getId() == user2Id), "Staff1 should have a chat with user2");

        // Retrieve unique chats for staff2.
        List<User> staff2Chats = staffMessagesRepo.getUniqueChats(staff2Id);
        // Staff2 should only have a chat with user2.
        assertEquals(1, staff2Chats.size(), "Staff2 should have exactly one unique chat");
        assertEquals(user2Id, staff2Chats.get(0).getId(), "Staff2's chat partner should be user2");
    }

    /**
     * Tests deletion of a staff message from the chat history.
     */
    @Test
    @Order(5)
    public void testDeleteStaffMessage() {
        // Insert a message to be deleted.
        Message msg = new Message(user1Id, "Message to be deleted");
        StaffMessage sm = staffMessagesRepo.sendMessage(msg, user1Id, staff1Id);
        assertNotNull(sm.getId(), "New message should have an ID");

        // Record the number of messages before deletion.
        List<StaffMessage> chatBeforeDeletion = staffMessagesRepo.loadChat(user1Id, staff1Id);
        int countBefore = chatBeforeDeletion.size();

        // Delete the newly added message.
        staffMessagesRepo.delete(sm.getId());

        // Verify that the chat history size decreases by one.
        List<StaffMessage> chatAfterDeletion = staffMessagesRepo.loadChat(user1Id, staff1Id);
        int countAfter = chatAfterDeletion.size();
        assertEquals(countBefore - 1, countAfter, "Chat history should have one less message after deletion");
    }
}