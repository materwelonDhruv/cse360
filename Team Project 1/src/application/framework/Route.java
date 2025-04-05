package application.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation that marks a class as a routed page.
 * <p>
 * This annotation associates a page class with a route, represented by an enum of type {@link MyPages}.
 * The route will be used by the {@link PageRouter} to navigate between pages.
 * </p>
 *
 * @author Dhruv
 * @see MyPages
 * @see PageRouter
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    /**
     * The route associated with the page.
     *
     * @return The {@link MyPages} enum value representing the route for the page.
     */
    MyPages value();
}