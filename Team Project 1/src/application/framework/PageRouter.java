package application.framework;

import javafx.stage.Stage;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Logger;

/**
 * Handles page navigation in the application.
 * <p>
 * This class is responsible for scanning pages annotated with {@link Route} in the
 * "application.pages" package, mapping them to the corresponding {@link MyPages} enum,
 * and maintaining a history stack for back navigation.
 * </p>
 *
 * @author Dhruv
 * @see Route
 * @see MyPages
 */
public class PageRouter {

    private static final Logger logger = Logger.getLogger(PageRouter.class.getName());

    private final Stage primaryStage;
    private final Map<MyPages, Class<? extends BasePage>> routeMap = new HashMap<>();


    // Stack of previously visited pages; top is the most recent
    private final Deque<MyPages> history = new ArrayDeque<>();
    private final MyPages[] pagesToIgnoreFromHistory = new MyPages[]{
            MyPages.WELCOME_LOGIN,
            MyPages.SETUP_LOGIN,
            MyPages.USER_LOGIN,
            MyPages.FIRST
    };
    // When true, skip pushing currentPage on the next navigate() call
    private boolean skipHistoryPush = false;
    // The currently displayed page
    private MyPages currentPage = null;

    /**
     * Constructs a {@code PageRouter} with the given primary stage,
     * and immediately registers all @Route-annotated pages.
     *
     * @param primaryStage the primary JavaFX Stage
     */
    public PageRouter(Stage primaryStage) {
        this.primaryStage = primaryStage;
        autoRegister();
    }

    /**
     * Scans "application.pages" (and subpackages) for classes annotated with {@link Route},
     * and populates the routeMap.
     */
    private void autoRegister() {
        String basePackage = "application.pages";
        // Configuration that scans the package and all its subpackages
        ConfigurationBuilder config = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(Scanners.TypesAnnotated)
                .filterInputsBy(new FilterBuilder().includePackage(basePackage));
        Reflections reflections = new Reflections(config);

        // find and register every @Route on a BasePage subclass
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Route.class);
        for (Class<?> cls : annotated) {
            if (BasePage.class.isAssignableFrom(cls)) {
                @SuppressWarnings("unchecked")
                Class<? extends BasePage> pageClass = (Class<? extends BasePage>) cls;
                Route ann = pageClass.getAnnotation(Route.class);
                MyPages pageEnum = ann.value();
                routeMap.put(pageEnum, pageClass);
                System.out.println("Registered route " + pageEnum + " -> " + pageClass.getName());
            }
        }
    }

    /**
     * Navigates to the specified page.
     * <p>
     * Always pushes the current page onto the history stack (if non-null and different)
     * before switching to the new page. Does not itself handle popping history.
     * </p>
     *
     * @param page the {@link MyPages} enum value to navigate to
     */
    public void navigate(MyPages page) {
        if (page == null) {
            logger.warning("Cannot navigate to null page");
            return;
        }

        if (!skipHistoryPush && currentPage != null && !currentPage.equals(page)) {
            if (Arrays.asList(pagesToIgnoreFromHistory).contains(page)) {
                history.clear();
            } else {
                history.push(currentPage);
            }
        }
        skipHistoryPush = false;

        Class<? extends BasePage> pageClass = routeMap.get(page);
        if (pageClass == null) {
            logger.warning("No route found for: " + page);
            return;
        }

        try {
            Constructor<? extends BasePage> ctor = pageClass.getDeclaredConstructor();
            BasePage instance = ctor.newInstance();
            instance.init(primaryStage);
            currentPage = page;
            instance.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns and removes the last page from the history stack.
     * <p>
     * This is used by the back-button logic. If there is no history, returns null.
     * </p>
     *
     * @return the previous {@link MyPages} enum value, or null if none
     */
    public MyPages getPreviousPage() {
        if (history.isEmpty()) return null;
        skipHistoryPush = true;
        return history.pop();
    }

    /**
     * Returns the currently displayed page.
     *
     * @return the current {@link MyPages} enum value
     */
    public MyPages getCurrentPage() {
        return currentPage;
    }
}