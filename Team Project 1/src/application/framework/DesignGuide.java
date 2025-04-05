package application.framework;

/**
 * Provides system-wide constants and utilities for consistent design.
 * <p>
 * This class defines various constants such as default width and height, as well as style constants
 * to be used throughout the application for UI consistency. All constants are static and final for global access.
 * </p>
 *
 * @author Dhruv
 */
public final class DesignGuide {

    /**
     * Default width for the application windows.
     */
    public static final int DEFAULT_WIDTH = 800;

    /**
     * Default height for the application windows.
     */
    public static final int DEFAULT_HEIGHT = 400;

    // Style constants

    /**
     * Padding style for UI elements.
     */
    public static final String MAIN_PADDING = "-fx-padding: 20;";

    /**
     * Alignment style for centering UI elements.
     */
    public static final String CENTER_ALIGN = "-fx-alignment: center;";

    /**
     * Style for the title label (font size and weight).
     */
    public static final String TITLE_LABEL = "-fx-font-size: 16px; -fx-font-weight: bold;";

    /**
     * Style for the error label (font size and color).
     */
    public static final String ERROR_LABEL = "-fx-text-fill: red; -fx-font-size: 12px;";

    /**
     * Private constructor to prevent instantiation of the class.
     */
    private DesignGuide() {
    }
}