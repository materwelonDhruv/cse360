package database.migration.tables;

import database.migration.BaseTable;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnswersTable extends BaseTable {

    @Override
    public String getTableName() {
        return "Answers";
    }

    @Override
    public Map<String, String> getExpectedColumns() {
        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("answerID", "INT AUTO_INCREMENT PRIMARY KEY");
        cols.put("messageID", "INT UNIQUE NOT NULL");  // References Messages table
        cols.put("questionID", "INT NULL");            // If top-level answer
        cols.put("parentAnswerID", "INT NULL");         // For threaded replies
        cols.put("isPinned", "BOOLEAN NOT NULL DEFAULT FALSE");
        return cols;
    }

    @Override
    public String[] getInlineConstraints() {
        return new String[]{
                "CONSTRAINT fk_answerMessage FOREIGN KEY (messageID) REFERENCES Messages(messageID) ON DELETE CASCADE",
                "CONSTRAINT fk_questionID FOREIGN KEY (questionID) REFERENCES Questions(questionID) ON DELETE CASCADE",
                "CONSTRAINT fk_parentAnswerID FOREIGN KEY (parentAnswerID) REFERENCES Answers(answerID) ON DELETE CASCADE"
        };
    }
}