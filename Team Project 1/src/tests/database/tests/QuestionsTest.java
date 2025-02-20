package src.tests.database.tests;

import org.junit.jupiter.api.*;
import src.database.model.entities.Question;
import src.database.model.entities.User;
import src.database.repository.repos.Questions;
import src.database.repository.repos.Users;
import src.tests.database.BaseDatabaseTest;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuestionsTest extends BaseDatabaseTest {

    private static Questions questionsRepo;

    @BeforeAll
    public static void setupQuestions() {
        // Create users for the questions
        Users userRepo = appContext.users();
        User user1 = new User("qUser1", "Question", "Asker", "somePassword", "asker@example.com", 0);
        userRepo.create(user1);

        User user2 = new User("qUser2", "Another", "Asker", "somePassword", "asker2@example.com", 0);
        userRepo.create(user2);

        // Now get the questions repo
        questionsRepo = appContext.questions();
    }

    @Test
    @Order(1)
    public void testCreateQuestion() {
        Question q = new Question(1, "JUnit Title", "JUnit Content");
        Question created = questionsRepo.create(q);

        Assertions.assertNotNull(created.getId(), "Question ID should be generated");
        Assertions.assertEquals(1, created.getUserId());
        Assertions.assertEquals("JUnit Title", created.getTitle());
        Assertions.assertEquals("JUnit Content", created.getContent());
    }

    @Test
    @Order(2)
    public void testGetQuestionById() {
        Question fetched = questionsRepo.getById(1);
        Assertions.assertNotNull(fetched, "Should find a question with ID=1");
        Assertions.assertEquals("JUnit Title", fetched.getTitle());
    }

    @Test
    @Order(3)
    public void testUpdateQuestion() {
        Question existing = questionsRepo.getById(1);
        existing.setTitle("Updated Title");
        existing.setContent("Updated Content");

        Question updated = questionsRepo.update(existing);
        Assertions.assertEquals("Updated Title", updated.getTitle());
        Assertions.assertEquals("Updated Content", updated.getContent());
    }

    @Test
    @Order(4)
    public void testGetAllQuestions() {
        List<Question> all = questionsRepo.getAll();
        Assertions.assertFalse(all.isEmpty(), "At least one question should exist");
    }

    @Test
    @Order(5)
    public void testNegativeUserId() {
        Question invalid = new Question(0, "No user", "Should fail");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            questionsRepo.create(invalid);
        }, "Should throw for invalid userID=0");
    }

    @Test
    @Order(6)
    public void testNegativeEmptyTitle() {
        Question invalid = new Question(1, "", "Empty title");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            questionsRepo.create(invalid);
        }, "Should throw for empty title");
    }

    @Test
    @Order(7)
    public void testDeleteQuestion() {
        questionsRepo.delete(1);
        Question deleted = questionsRepo.getById(1);
        Assertions.assertNull(deleted, "Question #1 should be deleted");
    }

    @Test
    @Order(8)
    public void testSearchQuestions() {
        // Create questions with "test" keyword and one without
        Question q1 = new Question(1, "Test question one", "This is a test content");
        Question q2 = new Question(1, "Another test", "More testing here");
        Question q3 = new Question(1, "Different topic", "No keyword here");
        questionsRepo.create(q1);
        questionsRepo.create(q2);
        questionsRepo.create(q3);

        // Search for "test" (case-insensitive)
        List<Question> results = questionsRepo.searchQuestions("test");
        Assertions.assertTrue(results.size() >= 2, "Expected at least two questions containing 'test'");
        for (Question q : results) {
            boolean containsKeyword = q.getTitle().toLowerCase().contains("test")
                    || q.getContent().toLowerCase().contains("test");
            Assertions.assertTrue(containsKeyword, "Each returned question must contain 'test'");
        }
    }

    @Test
    @Order(9)
    public void testGetQuestionsByUser() {
        // Create a question for userID = 2
        Question q = new Question(2, "User specific question", "Content by user 2");
        questionsRepo.create(q);

        List<Question> results = questionsRepo.getQuestionsByUser(2);
        Assertions.assertFalse(results.isEmpty(), "Expected at least one question for userID=2");
        for (Question ques : results) {
            Assertions.assertEquals(2, ques.getUserId(), "Each returned question should belong to userID=2");
        }
    }

    @Test
    @Order(10)
    public void testGetUnansweredQuestions() {
        // Create a new question that has no answers.
        Question q = new Question(1, "Unanswered question", "Content unanswered");
        questionsRepo.create(q);

        List<Question> unanswered = questionsRepo.getUnansweredQuestions();
        boolean found = unanswered.stream().anyMatch(question -> question.getTitle().equals("Unanswered question"));
        Assertions.assertTrue(found, "The unanswered question should be returned by getUnansweredQuestions");
    }
}
