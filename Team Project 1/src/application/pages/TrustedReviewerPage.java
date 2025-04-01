package application.pages;


import application.framework.*;
import database.model.entities.User;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

@Route(MyPages.TRUSTED_REVIEWER)
@View(title = "Manage Trusted Reviewers")
public class TrustedReviewerPage extends BasePage {
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Retrieve the active user from session.
        User user = context.getSession().getActiveUser();
        if (user == null) {
            return new VBox(new Label("No active user found."));
        }

        Label titleLabel = UIFactory.createLabel("Trusted Reviewers");
        // Navigation buttons using UIFactory; navigation via the shared router.
        Button backButton = UIFactory.createButton("Back", e -> e.routeToPage(MyPages.USER_HOME, context));

        layout.getChildren().addAll(titleLabel, backButton);
        return layout;
    }
}
