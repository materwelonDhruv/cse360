package application.framework;

import application.AppContext;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Base for all pages. Minimizes JavaFX boilerplate.
 * Also provides a protected 'context' for DB operations.
 */
public abstract class BasePage {

    protected final AppContext context;
    private Stage stage;

    public BasePage() {
        try {
            this.context = AppContext.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize AppContext", e);
        }
    }

    /**
     * Called by PageRouter after constructing pages via reflection.
     */
    public void init(Stage stage) {
        this.stage = stage;
    }

    /**
     * Subclasses override to build their UI layout.
     */
    public abstract Pane createView();

    /**
     * Called by PageRouter to show the page.
     */
    public void show() {
        Pane root = createView();
        Scene scene = new Scene(root, getWidth(), getHeight());
        stage.setScene(scene);
        stage.setTitle(getTitle());
        stage.setResizable(isResizable());
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    /**
     * If @View is present, use it; else default to 800x400, etc.
     */
    public String getTitle() {
        if (this.getClass().isAnnotationPresent(View.class)) {
            View v = this.getClass().getAnnotation(View.class);
            return v.title().isEmpty() ? "Untitled" : v.title();
        }
        return "Untitled";
    }

    public int getWidth() {
        if (this.getClass().isAnnotationPresent(View.class)) {
            return this.getClass().getAnnotation(View.class).width();
        }
        return 800;
    }

    public int getHeight() {
        if (this.getClass().isAnnotationPresent(View.class)) {
            return this.getClass().getAnnotation(View.class).height();
        }
        return 400;
    }

    public boolean isResizable() {
        if (this.getClass().isAnnotationPresent(View.class)) {
            return this.getClass().getAnnotation(View.class).resizable();
        }
        return true;
    }
}