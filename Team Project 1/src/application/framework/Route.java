package application.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a class as a routed page. The value is an enum of type MyPages.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    application.framework.MyPages value();
}