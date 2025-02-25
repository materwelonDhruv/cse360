package tests.database.tests;

import database.model.entities.Message;
import database.model.entities.Question;
import database.model.entities.User;
import database.repository.repos.Questions;
import database.repository.repos.Users;
import org.junit.jupiter.api.*;
import tests.database.BaseDatabaseTest;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuestionsTest extends BaseDatabaseTest {

    private static Questions questionsRepo;
    private static Users userRepo;
    private static Timestamp now;

    @BeforeAll
    public static void setupQuestions() {
        now = new Timestamp(System.currentTimeMillis());
        userRepo = appContext.users();
        // Create two users
        User user1 = new User("qUser1", "Question", "Asker", "somePassword", "asker@example.com", 0);
        userRepo.create(user1);
        User user2 = new User("qUser2", "Another", "Asker", "somePassword", "asker2@example.com", 0);
        userRepo.create(user2);

        questionsRepo = appContext.questions();
    }

    @Test
    @Order(1)
    public void testCreateQuestion() {
        Message msg = new Message(1, "JUnit Content");
        Question q = new Question(msg, "JUnit Title");
        Question created = questionsRepo.create(q);

        assertNotNull(created.getId(), "Question ID should be generated");
        assertEquals(1, created.getMessage().getUserId());
        assertEquals("JUnit Title", created.getTitle());
        assertEquals("JUnit Content", created.getMessage().getContent());
    }

    @Test
    @Order(2)
    public void testGetQuestionById() {
        Question fetched = questionsRepo.getById(1);
        assertNotNull(fetched, "Should fetch question with ID=1");
        assertEquals("JUnit Title", fetched.getTitle());
    }

    @Test
    @Order(3)
    public void testUpdateQuestion() {
        Question existing = questionsRepo.getById(1);
        existing.setTitle("Updated Title");
        existing.getMessage().setContent("Updated Content");
        Question updated = questionsRepo.update(existing);
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Content", updated.getMessage().getContent());
    }

    @Test
    @Order(4)
    public void testGetAllQuestions() {
        List<Question> all = questionsRepo.getAll();
        assertFalse(all.isEmpty(), "At least one question should exist");
    }

    @Test
    @Order(5)
    public void testNegativeUserId() {
        Message invalidMsg = new Message(0, "No user");
        Question invalid = new Question(invalidMsg, "Should fail");
        assertThrows(IllegalArgumentException.class, () -> {
            questionsRepo.create(invalid);
        }, "Should throw for invalid userID=0");
    }

    @Test
    @Order(6)
    public void testNegativeEmptyTitle() {
        Message msg = new Message(1, "Content for empty title");
        Question invalid = new Question(msg, "");
        assertThrows(IllegalArgumentException.class, () -> {
            questionsRepo.create(invalid);
        }, "Should throw for empty title");
    }

    @Test
    @Order(7)
    public void testDeleteQuestion() {
        questionsRepo.delete(1);
        Question deleted = questionsRepo.getById(1);
        assertNull(deleted, "Question #1 should be deleted");
    }

    @Test
    @Order(8)
    public void testSearchQuestions() throws Exception {
        // Create questions with "test" keyword and one without
        Message msg1 = new Message(1, "This is a test content");
        Message msg2 = new Message(1, "More testing here");
        Message msg3 = new Message(1, "No keyword here");
        questionsRepo.create(new Question(msg1, "Test question one"));
        questionsRepo.create(new Question(msg2, "Another test"));
        questionsRepo.create(new Question(msg3, "Different topic"));

        List<Question> results = questionsRepo.searchQuestions("test");
        assertTrue(results.size() >= 2, "Expected at least two questions containing 'test'");
        for (Question q : results) {
            String combined = q.getTitle().toLowerCase() + " " + q.getMessage().getContent().toLowerCase();
            assertTrue(combined.contains("test"), "Each question must contain 'test'");
        }
    }

    @Test
    @Order(9)
    public void testGetQuestionsByUser() {
        Message msg = new Message(2, "Content by user 2");
        Question q = new Question(msg, "User specific question");
        questionsRepo.create(q);

        List<Question> results = questionsRepo.getQuestionsByUser(2);
        assertFalse(results.isEmpty(), "Expected at least one question for userID=2");
        for (Question ques : results) {
            assertEquals(2, ques.getMessage().getUserId(), "Each question should belong to userID=2");
        }
    }

    @Test
    @Order(10)
    public void testGetUnansweredQuestions() {
        Message msg = new Message(1, "Unanswered content");
        Question q = new Question(msg, "Unanswered question");
        questionsRepo.create(q);

        List<Question> unanswered = questionsRepo.getUnansweredQuestions();
        boolean found = unanswered.stream().anyMatch(question -> question.getTitle().equals("Unanswered question"));
        assertTrue(found, "Unanswered question should be returned by getUnansweredQuestions");
    }

    @Test
    @Order(11)
    public void testUpdateQuestionTitle() {
        Question existing = questionsRepo.getById(2);
        Question updated = questionsRepo.updateQuestionTitle(existing.getId(), "New Title");
        assertEquals("New Title", updated.getTitle());
    }

    @Test
    @Order(12)
    public void testUpdateQuestionContent() {
        Question existing = questionsRepo.getById(2);
        Question updated = questionsRepo.updateQuestionContent(existing.getId(), "New Content");
        assertEquals("New Content", updated.getMessage().getContent());
    }
}