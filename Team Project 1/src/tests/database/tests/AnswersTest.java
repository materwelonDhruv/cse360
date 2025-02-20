package src.tests.database.tests;

import org.junit.jupiter.api.*;
import src.database.model.entities.Answer;
import src.database.model.entities.Question;
import src.database.model.entities.User;
import src.database.repository.repos.Answers;
import src.database.repository.repos.Questions;
import src.database.repository.repos.Users;
import src.tests.database.BaseDatabaseTest;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AnswersTest extends BaseDatabaseTest {

    private static Answers answersRepo;

    @BeforeAll
    public static void setupAnswers() {
        // 1) Create two users: userID=1 (question owner), userID=2 (answer owner)
        Users userRepo = appContext.users();

        User questionUser = new User("answerTestQ", "Question", "Owner", "pwQ", "question@example.com", 0);
        userRepo.create(questionUser);

        User answerUser = new User("answerTestA", "Answer", "Owner", "pwA", "answer@example.com", 0);
        userRepo.create(answerUser);

        // 2) Create a sample question so that answer references are valid
        Questions questionsRepo = appContext.questions();
        Question q = new Question(1, "What is JUnit?", "Explain in detail"); // userID=1
        questionsRepo.create(q);

        // 3) Answers repository
        answersRepo = appContext.answers();
    }

    @Test
    @Order(1)
    public void testCreateTopLevelAnswer() {
        // userID=2 is answering questionID=1
        Answer ans = new Answer(2, "JUnit is a testing framework.", 1, null);
        Answer created = answersRepo.create(ans);

        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(2, created.getUserId());
        Assertions.assertEquals("JUnit is a testing framework.", created.getContent());
        Assertions.assertEquals(1, created.getQuestionId());
        Assertions.assertNull(created.getParentAnswerId());
    }

    @Test
    @Order(2)
    public void testCreateNestedAnswer() {
        // userID=2 replying to answerID=1
        Answer reply = new Answer(2, "Yes, JUnit 5 is the latest version!", null, 1);
        Answer created = answersRepo.create(reply);

        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals(2, created.getUserId());
        Assertions.assertNull(created.getQuestionId());
        Assertions.assertEquals(1, created.getParentAnswerId());
    }

    @Test
    @Order(3)
    public void testFetchAnswer() {
        Answer fetched = answersRepo.getById(1);
        Assertions.assertNotNull(fetched, "Should fetch an existing answer");
        Assertions.assertEquals(1, fetched.getId());
        Assertions.assertEquals("JUnit is a testing framework.", fetched.getContent());
    }

    @Test
    @Order(4)
    public void testUpdateAnswer() {
        Answer existing = answersRepo.getById(1);
        existing.setContent("JUnit is a popular testing framework for Java.");
        Answer updated = answersRepo.update(existing);

        Assertions.assertEquals("JUnit is a popular testing framework for Java.", updated.getContent());
    }

    @Test
    @Order(5)
    public void testGetAnswersByQuestionId() {
        List<Answer> answers = answersRepo.getAnswersByQuestionId(1);
        Assertions.assertFalse(answers.isEmpty(), "Should have at least one top-level answer");
        Assertions.assertEquals(1, answers.get(0).getQuestionId());
    }

    @Test
    @Order(6)
    public void testGetRepliesByAnswerId() {
        List<Answer> replies = answersRepo.getRepliesByAnswerId(1);
        Assertions.assertFalse(replies.isEmpty(), "Should have at least one reply");
        Assertions.assertEquals(1, replies.get(0).getParentAnswerId());
    }

    @Test
    @Order(7)
    public void testNegativeUserId() {
        Answer invalid = new Answer(0, "Content", 1, null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            answersRepo.create(invalid);
        }, "Expected exception for invalid userID=0");
    }

    @Test
    @Order(8)
    public void testNegativeEmptyContent() {
        // userID=2, but content is empty
        Answer invalid = new Answer(2, "", 1, null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            answersRepo.create(invalid);
        }, "Expected exception for empty content");
    }

    @Test
    @Order(9)
    public void testCreateAnswerWithBothQuestionAndParentFails() {
        // userID=2
        Answer invalid = new Answer(2, "Invalid answer", 1, 2);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            answersRepo.create(invalid);
        }, "Should throw for referencing both questionID and parentAnswerID");
    }

    @Test
    @Order(10)
    public void testDeleteAnswer() {
        answersRepo.delete(1);
        Answer deleted = answersRepo.getById(1);
        Assertions.assertNull(deleted, "Answer #1 should be deleted");
    }

    @Test
    @Order(11)
    public void testSearchAnswers() {
        // Create several answers with the keyword "search"
        Answer a1 = new Answer(2, "This answer is for search test", 1, null);
        Answer a2 = new Answer(2, "Another answer with SEARCH keyword", 1, null);
        Answer a3 = new Answer(2, "Irrelevant answer", 1, null);

        answersRepo.create(a1);
        answersRepo.create(a2);
        answersRepo.create(a3);

        List<Answer> results = answersRepo.searchAnswers("search");

        Assertions.assertTrue(results.size() >= 2, "Expected at least two answers containing 'search'");
        for (Answer a : results) {
            Assertions.assertTrue(a.getContent().toLowerCase().contains("search"),
                    "Each returned answer must contain the keyword 'search'");
        }
    }


    @Test
    @Order(12)
    public void testGetAnswersByUser() {
        // Create an answer for userID = 2
        Answer a = new Answer(2, "Answer from user 2", 1, null);
        answersRepo.create(a);

        List<Answer> results = answersRepo.getAnswersByUser(2);
        Assertions.assertFalse(results.isEmpty(), "Expected at least one answer by userID=2");
        for (Answer ans : results) {
            Assertions.assertEquals(2, ans.getUserId(), "Each returned answer should be posted by userID=2");
        }
    }

}
