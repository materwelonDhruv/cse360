package application.framework;

import application.AppContext;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Base class for all pages in the application.
 * <p>
 * This class minimizes JavaFX boilerplate code and provides a protected {@link AppContext} instance for
 * performing database operations. It handles basic scene setup and layout management.
 * </p>
 *
 * @author Dhruv
 */
public abstract class BasePage {

    /**
     * The {@link AppContext} instance provides access to the application context.
     * <p>
     * It is used for interacting with various application components like repositories,
     * the session, and the router, enabling database operations and navigation within the app.
     * </p>
     *
     * @see AppContext
     */
    protected final AppContext context;
    private Stage stage;

    /**
     * Initializes the base page and the app context.
     * <p>
     * It retrieves the singleton instance of {@link AppContext} to provide access to the application context.
     * </p>
     *
     * @throws RuntimeException if AppContext fails to initialize
     */
    public BasePage() {
        try {
            this.context = AppContext.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize AppContext", e);
        }
    }

    /**
     * Initializes the page by setting the stage.
     * <p>
     * This method is called by the {@link PageRouter} after the page is constructed.
     * </p>
     *
     * @param stage The primary stage for the page.
     */
    public void init(Stage stage) {
        this.stage = stage;
    }

    /**
     * Abstract method that subclasses must override to build their UI layout.
     * <p>
     * Subclasses should define how their specific view layout will be constructed.
     * </p>
     *
     * @return The root pane for the page's layout.
     */
    public abstract Pane createView();

    /**
     * Displays the page by setting up the scene and showing the stage.
     * <p>
     * This method sets the scene, title, size, and visibility for the page.
     * </p>
     */
    public void show() {
        Pane root = createView();
        Scene scene = new Scene(root, getWidth(), getHeight());
        stage.setScene(scene);
        stage.setTitle(getTitle());
        stage.setResizable(isResizable());
        stage.show();
    }

    /**
     * Returns the stage for the page.
     *
     * @return The {@link Stage} associated with the page.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Returns the title of the page, either from the {@link View} annotation or default value.
     *
     * @return The title of the page.
     */
    public String getTitle() {
        if (this.getClass().isAnnotationPresent(application.framework.View.class)) {
            application.framework.View v = this.getClass().getAnnotation(application.framework.View.class);
            return v.title().isEmpty() ? "Untitled" : v.title();
        }
        return "Untitled";
    }

    /**
     * Returns the width of the page, either from the {@link View} annotation or default value.
     *
     * @return The width of the page.
     */
    public int getWidth() {
        if (this.getClass().isAnnotationPresent(application.framework.View.class)) {
            return this.getClass().getAnnotation(application.framework.View.class).width();
        }
        return application.framework.DesignGuide.DEFAULT_WIDTH;
    }

    /**
     * Returns the height of the page, either from the {@link View} annotation or default value.
     *
     * @return The height of the page.
     */
    public int getHeight() {
        if (this.getClass().isAnnotationPresent(application.framework.View.class)) {
            return this.getClass().getAnnotation(application.framework.View.class).height();
        }
        return application.framework.DesignGuide.DEFAULT_HEIGHT;
    }

    /**
     * Returns whether the page is resizable, either from the {@link View} annotation or default value.
     *
     * @return {@code true} if the page is resizable, {@code false} otherwise.
     */
    public boolean isResizable() {
        if (this.getClass().isAnnotationPresent(application.framework.View.class)) {
            return this.getClass().getAnnotation(application.framework.View.class).resizable();
        }
        return true;
    }
}