package tests.database.tests;

import database.model.entities.Message;
import database.model.entities.Question;
import database.model.entities.User;
import database.repository.repos.Questions;
import database.repository.repos.ReadMessages;
import database.repository.repos.Users;
import org.junit.jupiter.api.*;
import tests.database.BaseDatabaseTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReadMessagesTest extends BaseDatabaseTest {

    private static ReadMessages readMessagesRepo;
    private static Users userRepo;
    private static Questions questionsRepo;
    private static int testUserId;

    @BeforeAll
    public static void setupReadMessages() {
        userRepo = appContext.users();
        // Create a test user.
        User user = new User("readTestUser", "Test", "User", "password", "readtest@example.com", 0);
        userRepo.create(user);
        testUserId = user.getId();

        questionsRepo = appContext.questions();
        readMessagesRepo = appContext.readMessages();
        // Create one dummy question and mark its underlying message as read.
        Message msg = new Message(testUserId, "Dummy question content");
        Question question = new Question(msg, "Dummy Question");
        question = questionsRepo.create(question);
        int dummyMessageId = question.getMessage().getId();
        readMessagesRepo.markAsRead(testUserId, dummyMessageId);
    }

    @Test
    @Order(1)
    public void testMarkAsReadSingle() {
        // Create a new question to generate a valid message.
        Message msg = new Message(testUserId, "Another dummy question");
        Question question = new Question(msg, "Another Dummy Question");
        question = questionsRepo.create(question);
        int messageId = question.getMessage().getId();

        // Mark the message as read using the single markAsRead.
        readMessagesRepo.markAsRead(testUserId, messageId);

        // Verify using findReadMessages.
        List<Integer> readMsgs = readMessagesRepo.findReadMessages(testUserId, List.of(messageId));
        assertTrue(readMsgs.contains(messageId), "Message should be marked as read");
    }

    @Test
    @Order(2)
    public void testMarkAsReadList() {
        // Create two new questions.
        Message msg1 = new Message(testUserId, "List dummy question 1");
        Question q1 = new Question(msg1, "List Dummy 1");
        q1 = questionsRepo.create(q1);
        int m1 = q1.getMessage().getId();

        Message msg2 = new Message(testUserId, "List dummy question 2");
        Question q2 = new Question(msg2, "List Dummy 2");
        q2 = questionsRepo.create(q2);
        int m2 = q2.getMessage().getId();

        // Mark both as read using the list variant.
        readMessagesRepo.markAsRead(testUserId, Arrays.asList(m1, m2));

        List<Integer> readMsgs = readMessagesRepo.findReadMessages(testUserId, Arrays.asList(m1, m2));
        assertEquals(2, readMsgs.size(), "Both messages should be marked as read");
        assertTrue(readMsgs.contains(m1), "Should contain first message");
        assertTrue(readMsgs.contains(m2), "Should contain second message");
    }

    @Test
    @Order(3)
    public void testMarkAsUnread() {
        // Create a question, mark it as read, then mark as unread.
        Message msg = new Message(testUserId, "To be unread question");
        Question question = new Question(msg, "To be Unread");
        question = questionsRepo.create(question);
        int messageId = question.getMessage().getId();
        readMessagesRepo.markAsRead(testUserId, messageId);

        // Mark it as unread.
        readMessagesRepo.markAsUnread(testUserId, messageId);

        List<Integer> readMsgs = readMessagesRepo.findReadMessages(testUserId, List.of(messageId));
        assertFalse(readMsgs.contains(messageId), "Message should be marked as unread");
    }

    @Test
    @Order(4)
    public void testFindReadMessages() {
        // Create two questions.
        Message msg1 = new Message(testUserId, "Find test question 1");
        Question q1 = new Question(msg1, "Find Test 1");
        q1 = questionsRepo.create(q1);
        int m1 = q1.getMessage().getId();

        Message msg2 = new Message(testUserId, "Find test question 2");
        Question q2 = new Question(msg2, "Find Test 2");
        q2 = questionsRepo.create(q2);
        int m2 = q2.getMessage().getId();

        // Mark both as read.
        readMessagesRepo.markAsRead(testUserId, m1);
        readMessagesRepo.markAsRead(testUserId, m2);

        // Check findReadMessages with a list containing m1, m2, and one extra id.
        List<Integer> queryList = Arrays.asList(m1, m2, 99999);
        List<Integer> found = readMessagesRepo.findReadMessages(testUserId, queryList);
        assertEquals(2, found.size(), "Expected exactly two read messages");
        assertTrue(found.contains(m1), "Should contain message m1");
        assertTrue(found.contains(m2), "Should contain message m2");
    }
}