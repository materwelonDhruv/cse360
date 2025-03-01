package application.framework;

import javafx.stage.Stage;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Finds pages annotated with @Route(MyPages.XYZ) and sets up navigation.
 */
public class PageRouter {

    private static final Logger logger = Logger.getLogger(PageRouter.class.getName());
    private final Stage primaryStage;
    private final Map<application.framework.MyPages, Class<? extends application.framework.BasePage>> routeMap = new HashMap<>();

    public PageRouter(Stage primaryStage) {
        this.primaryStage = primaryStage;
        autoRegister();
    }

    private void autoRegister() {
        // 1) Use Reflections to scan package "application.pages" for annotated classes
        Reflections reflections = new Reflections("application.pages", Scanners.TypesAnnotated);
        // 2) Find all classes with @Route
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(application.framework.Route.class);

        for (Class<?> cls : annotated) {
            if (application.framework.BasePage.class.isAssignableFrom(cls)) {
                @SuppressWarnings("unchecked")
                Class<? extends application.framework.BasePage> pageClass = (Class<? extends application.framework.BasePage>) cls;
                application.framework.Route routeAnnotation = pageClass.getAnnotation(application.framework.Route.class);
                application.framework.MyPages pageEnum = routeAnnotation.value();
                routeMap.put(pageEnum, pageClass);
                System.out.println("Registered route " + pageEnum + " -> " + pageClass.getName());
            }
        }
    }

    /**
     * Navigate to the specified route in MyPages enum.
     */
    public void navigate(application.framework.MyPages page) {
        Class<? extends BasePage> pageClass = routeMap.get(page);
        if (pageClass == null) {
            logger.warning("No route found for: " + page);
            return;
        }
        try {
            Constructor<? extends BasePage> ctor = pageClass.getDeclaredConstructor();
            BasePage pageInstance = ctor.newInstance();
            pageInstance.init(primaryStage);
            pageInstance.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}