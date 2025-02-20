package src.database.migration.tables;

import src.database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Creates or syncs the Answers table.
 * An Answer can reference either a Question (top-level answer) or another Answer (threaded reply).
 */
public class AnswersTable extends BaseTable {

    @Override
    public String getTableName() {
        return "Answers";
    }

    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("answerID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("userID", "INT NOT NULL");
        cols.put("content", "TEXT NOT NULL");
        cols.put("questionID", "INT NULL");
        cols.put("parentAnswerID", "INT NULL");
        cols.put("createdAt", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
        return cols;
    }

    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_answerUser FOREIGN KEY (userID) REFERENCES Users(userID) ON DELETE CASCADE",
                "CONSTRAINT fk_questionID FOREIGN KEY (questionID) REFERENCES Questions(questionID) ON DELETE CASCADE",
                "CONSTRAINT fk_parentAnswerID FOREIGN KEY (parentAnswerID) REFERENCES Answers(answerID) ON DELETE CASCADE"
        };
    }
}
