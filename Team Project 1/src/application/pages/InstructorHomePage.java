package application.pages;


import application.framework.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

@Route(MyPages.INSTRUCTOR_HOME)
@View(title = "Instructor Page")
public class InstructorHomePage extends BasePage {
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);


        Label adminLabel = UIFactory.createLabel("Hello, Instructor!");
        // Navigation buttons using UIFactory; navigation via the shared router.
        Button logoutButton = UIFactory.createButton("Logout", e -> e.routeToPage(MyPages.USER_LOGIN, context));

        layout.getChildren().addAll(adminLabel, logoutButton);
        return layout;
    }
}
