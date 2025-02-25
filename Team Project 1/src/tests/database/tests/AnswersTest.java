package tests.database.tests;

import database.model.entities.Answer;
import database.model.entities.Message;
import database.model.entities.Question;
import database.model.entities.User;
import database.repository.repos.Answers;
import database.repository.repos.Questions;
import database.repository.repos.Users;
import org.junit.jupiter.api.*;
import tests.database.BaseDatabaseTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AnswersTest extends BaseDatabaseTest {

    private static Answers answersRepo;
    private static Questions questionsRepo;
    private static Users userRepo;

    @BeforeAll
    public static void setupAnswers() {
        userRepo = appContext.users();
        // Create two users: one for questions (ID=1) and one for answers (ID=2)
        User questionUser = new User("answerTestQ", "Question", "Owner", "pwQ", "question@example.com", 0);
        userRepo.create(questionUser);
        User answerUser = new User("answerTestA", "Answer", "Owner", "pwA", "answer@example.com", 0);
        userRepo.create(answerUser);

        // Create a sample question so that answer references are valid.
        questionsRepo = appContext.questions();
        Question q = new Question(new Message(1, "What is JUnit?"), "What is JUnit?");
        questionsRepo.create(q); // Assume questionID = generated value (likely 1)

        answersRepo = appContext.answers();
    }

    @Test
    @Order(1)
    public void testCreateTopLevelAnswer() {
        // Top-level answer: Message with userID=2, questionId=1, no parent.
        Message msg = new Message(2, "JUnit is a testing framework.");
        Answer ans = new Answer(msg, 1, null, false);
        Answer created = answersRepo.create(ans);

        assertNotNull(created.getId(), "Answer ID should be generated");
        assertEquals(2, created.getMessage().getUserId());
        assertEquals("JUnit is a testing framework.", created.getMessage().getContent());
        assertEquals(1, created.getQuestionId());
        assertNull(created.getParentAnswerId());
    }

    @Test
    @Order(2)
    public void testCreateNestedAnswer() {
        // Nested answer: reply to answer with ID=1 (parentAnswerID=1), questionId is null.
        Message msg = new Message(2, "Reply: JUnit 5 is the latest version!");
        Answer reply = new Answer(msg, null, 1, false);
        Answer created = answersRepo.create(reply);

        assertNotNull(created.getId(), "Nested Answer ID should be generated");
        assertEquals(2, created.getMessage().getUserId());
        assertNull(created.getQuestionId());
        assertEquals(1, created.getParentAnswerId());
    }

    @Test
    @Order(3)
    public void testFetchAnswer() {
        Answer fetched = answersRepo.getById(1);
        assertNotNull(fetched, "Should fetch existing answer");
        assertEquals(1, fetched.getId());
        assertEquals("JUnit is a testing framework.", fetched.getMessage().getContent());
    }

    @Test
    @Order(4)
    public void testUpdateAnswer() {
        Answer existing = answersRepo.getById(1);
        // Update content via underlying Message
        existing.getMessage().setContent("JUnit is a popular testing framework for Java.");
        Answer updated = answersRepo.update(existing);
        assertEquals("JUnit is a popular testing framework for Java.", updated.getMessage().getContent());
    }

    @Test
    @Order(5)
    public void testTogglePin() {
        Answer a = answersRepo.getById(1);
        boolean initialPin = a.getIsPinned();
        Answer toggled = answersRepo.togglePin(a.getId());
        assertEquals(!initialPin, toggled.getIsPinned(), "Pinned state should toggle");
    }

    @Test
    @Order(6)
    public void testUpdateAnswerContent() {
        Answer a = answersRepo.getById(1);
        Answer updated = answersRepo.updateAnswerContent(a.getId(), "Updated answer content.");
        assertEquals("Updated answer content.", updated.getMessage().getContent());
    }

    @Test
    @Order(7)
    public void testGetAnswersByUser() {
        // Create an additional answer for userID=2.
        Message msg = new Message(2, "Another answer from user 2");
        Answer a = new Answer(msg, 1, null, false);
        answersRepo.create(a);

        List<Answer> results = answersRepo.getAnswersByUser(2);
        assertFalse(results.isEmpty(), "Expected at least one answer by userID=2");
        for (Answer ans : results) {
            assertEquals(2, ans.getMessage().getUserId(), "Answer should be by userID=2");
        }
    }

    @Test
    @Order(8)
    public void testNegativeUserId() {
        Message invalidMsg = new Message(0, "Content");
        Answer invalid = new Answer(invalidMsg, 1, null, false);
        assertThrows(IllegalArgumentException.class, () -> {
            answersRepo.create(invalid);
        }, "Expected exception for invalid userID=0");
    }

    @Test
    @Order(9)
    public void testNegativeEmptyContent() {
        Message invalidMsg = new Message(2, "");
        Answer invalid = new Answer(invalidMsg, 1, null, false);
        assertThrows(IllegalArgumentException.class, () -> {
            answersRepo.create(invalid);
        }, "Expected exception for empty content");
    }

    @Test
    @Order(10)
    public void testCreateAnswerWithBothQuestionAndParentFails() {
        Message msg = new Message(2, "Invalid answer");
        // Both questionId and parentAnswerId set should be invalid.
        Answer invalid = new Answer(msg, 1, 2, false);
        assertThrows(IllegalArgumentException.class, () -> {
            answersRepo.create(invalid);
        }, "Should throw for both questionID and parentAnswerID set");
    }

    @Test
    @Order(11)
    public void testDeleteAnswer() {
        answersRepo.delete(1);
        Answer deleted = answersRepo.getById(1);
        assertNull(deleted, "Answer #1 should be deleted");
    }

    @Test
    @Order(12)
    public void testSearchAnswers() throws Exception {
        Message msg1 = new Message(2, "This answer is for search test");
        Message msg2 = new Message(2, "Another answer with SEARCH keyword");
        Message msg3 = new Message(2, "Irrelevant answer");
        answersRepo.create(new Answer(msg1, 1, null, false));
        answersRepo.create(new Answer(msg2, 1, null, false));
        answersRepo.create(new Answer(msg3, 1, null, false));

        List<Answer> results = answersRepo.searchAnswers("search");
        assertTrue(results.size() >= 2, "Expected at least two answers containing 'search'");
        for (Answer a : results) {
            assertTrue(a.getMessage().getContent().toLowerCase().contains("search"),
                    "Each answer must contain 'search'");
        }
    }
}