package application.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Optional annotation for specifying window metadata like title, width, and height.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface View {
    String title() default "";

    int width() default 800;

    int height() default 400;

    boolean resizable() default true;
}