package database.tests;

import database.BaseDatabaseTest;
import database.model.entities.Message;
import database.model.entities.PrivateMessage;
import database.model.entities.Question;
import database.model.entities.User;
import database.repository.repos.PrivateMessages;
import database.repository.repos.Questions;
import database.repository.repos.Users;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PrivateMessages} repository.
 * <p>
 * This test class verifies various operations performed on the PrivateMessages repository,
 * including creation, retrieval, updating, deletion, searching, and fetching messages by user.
 * </p>
 *
 * <p>
 * It interacts with the {@link Users} and {@link Questions} repositories to ensure messages
 * are created, related to specific questions, and queried correctly.
 * </p>
 *
 * @author Dhruv
 * @see PrivateMessages
 * @see Users
 * @see Questions
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrivateMessagesTest extends BaseDatabaseTest {

    private static PrivateMessages pmRepo;
    // Store the dummy question's generated ID for use in PrivateMessage creation.
    private static int dummyQuestionId;

    /**
     * Sets up the repository instances and initializes users and a dummy question for testing.
     */
    @BeforeAll
    public static void setupPrivateMessages() {
        Users userRepo = appContext.users();

        // Create two users: one as sender (ID=1) and one as receiver (ID=2)
        User sender = new User("pmUser1", "Private", "Sender", "pw1", "pm1@example.com", 0);
        userRepo.create(sender);
        User receiver = new User("pmUser2", "Private", "Receiver", "pw2", "pm2@example.com", 0);
        userRepo.create(receiver);

        // Create a dummy question to satisfy the foreign key constraint on PrivateMessages.
        Questions questionsRepo = appContext.questions();
        Message qMsg = new Message(1, "Dummy question content for PM tests.");
        Question dummyQuestion = new Question(qMsg, "Dummy Question Title");
        dummyQuestion = questionsRepo.create(dummyQuestion);
        dummyQuestionId = dummyQuestion.getId();

        pmRepo = appContext.privateMessages();
    }

    /**
     * Tests creating a new private message.
     */
    @Test
    @Order(1)
    public void testCreatePrivateMessage() {
        // Create a PrivateMessage using the dummy question ID.
        Message msg = new Message(1, "This is a private message.");
        PrivateMessage pm = new PrivateMessage(msg, dummyQuestionId, null);
        PrivateMessage created = pmRepo.create(pm);

        assertEquals(1, created.getMessage().getUserId());
        assertEquals("This is a private message.", created.getMessage().getContent());
        assertEquals(dummyQuestionId, created.getQuestionId());
    }

    /**
     * Tests fetching a private message by its ID.
     */
    @Test
    @Order(2)
    public void testGetPrivateMessageById() {
        PrivateMessage fetched = pmRepo.getById(1);
        assertNotNull(fetched, "Should fetch PrivateMessage with ID=1");
        assertEquals("This is a private message.", fetched.getMessage().getContent());
    }

    /**
     * Tests updating the content of an existing private message.
     */
    @Test
    @Order(3)
    public void testUpdatePrivateMessage() {
        PrivateMessage existing = pmRepo.getById(1);
        existing.getMessage().setContent("Updated private message content.");
        PrivateMessage updated = pmRepo.update(existing);
        assertEquals("Updated private message content.", updated.getMessage().getContent());
    }

    /**
     * Tests fetching all private messages from the repository.
     */
    @Test
    @Order(4)
    public void testGetAllPrivateMessages() {
        List<PrivateMessage> all = pmRepo.getAll();
        assertFalse(all.isEmpty(), "Expected at least one private message");
    }

    /**
     * Tests retrieving private messages associated with a specific user.
     */
    @Test
    @Order(5)
    public void testGetPrivateMessagesByUser() {
        Message msg = new Message(2, "Message by user 2");
        PrivateMessage pm = new PrivateMessage(msg, dummyQuestionId, null);
        pmRepo.create(pm);

        List<PrivateMessage> results = pmRepo.getPrivateMessagesByUser(2);
        assertFalse(results.isEmpty(), "Expected at least one private message for userID=2");
        for (PrivateMessage p : results) {
            assertEquals(2, p.getMessage().getUserId(), "PrivateMessage should be from userID=2");
        }
    }

    /**
     * Tests searching for private messages containing a specific keyword.
     *
     * @throws Exception if an error occurs during the search operation.
     */
    @Test
    @Order(6)
    public void testSearchPrivateMessages() throws Exception {
        Message msg1 = new Message(1, "Search test message one");
        Message msg2 = new Message(1, "Another SEARCH message");
        Message msg3 = new Message(1, "Unrelated content");
        pmRepo.create(new PrivateMessage(msg1, dummyQuestionId, null));
        pmRepo.create(new PrivateMessage(msg2, dummyQuestionId, null));
        pmRepo.create(new PrivateMessage(msg3, dummyQuestionId, null));

        List<PrivateMessage> results = pmRepo.searchPrivateMessages("search");
        assertTrue(results.size() >= 2, "Expected at least two private messages with 'search'");
        for (PrivateMessage p : results) {
            assertTrue(p.getMessage().getContent().toLowerCase().contains("search"),
                    "Each message must contain 'search'");
        }
    }

    /**
     * Tests deleting a private message by its ID.
     */
    @Test
    @Order(7)
    public void testDeletePrivateMessage() {
        pmRepo.delete(1);
        PrivateMessage deleted = pmRepo.getById(1);
        assertNull(deleted, "PrivateMessage #1 should be deleted");
    }
}