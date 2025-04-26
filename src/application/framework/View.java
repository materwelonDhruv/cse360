package application.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Optional annotation for specifying window metadata like title, width, and height.
 * <p>
 * This annotation can be applied to pages to specify the window title, default width, height, and whether the window is resizable.
 * </p>
 *
 * @author Dhruv
 * @see DesignGuide
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface View {
    /**
     * The title of the window.
     *
     * @return The title of the window.
     */
    String title() default "";

    /**
     * The width of the window.
     *
     * @return The width of the window, defaulting to {@link DesignGuide#DEFAULT_WIDTH}.
     */
    int width() default DesignGuide.DEFAULT_WIDTH;

    /**
     * The height of the window.
     *
     * @return The height of the window, defaulting to {@link DesignGuide#DEFAULT_HEIGHT}.
     */
    int height() default DesignGuide.DEFAULT_HEIGHT;

    /**
     * Whether the window is resizable.
     *
     * @return {@code true} if the window is resizable, {@code false} otherwise.
     */
    boolean resizable() default true;
}