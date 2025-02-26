package src.application.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Optional annotation for specifying window metadata like title, width, and height.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface View {
    String title() default "";

    int width() default DesignGuide.DEFAULT_WIDTH;

    int height() default DesignGuide.DEFAULT_HEIGHT;

    boolean resizable() default true;
}