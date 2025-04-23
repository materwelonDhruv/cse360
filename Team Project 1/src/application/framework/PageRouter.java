package application.framework;

import javafx.stage.Stage;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Handles page navigation in the application.
 * <p>
 * This class is responsible for scanning pages annotated with {@link Route} in the "application.pages" package,
 * mapping them to the corresponding {@link MyPages} enum, and handling the navigation between pages.
 * </p>
 *
 * @author Dhruv
 * @see Route
 * @see MyPages
 */
public class PageRouter {

    private static final Logger logger = Logger.getLogger(PageRouter.class.getName());
    private final Stage primaryStage;
    private final Map<application.framework.MyPages, Class<? extends application.framework.BasePage>> routeMap = new HashMap<>();
    private MyPages previousPage = null;
    private MyPages currentPage = null;

    /**
     * Constructs a {@code PageRouter} with the given primary stage.
     * <p>
     * This constructor initializes the primary stage and automatically registers all pages annotated with
     * {@link Route} in the "application.pages" package.
     * </p>
     *
     * @param primaryStage The primary stage of the application.
     */
    public PageRouter(Stage primaryStage) {
        this.primaryStage = primaryStage;
        autoRegister();
    }

    /**
     * Automatically registers pages annotated with {@link Route} from the "application.pages" package.
     * <p>
     * This method uses the Reflections library to scan the package (and subpackages) for classes with the {@link Route} annotation,
     * mapping them to the corresponding {@link MyPages} enum value.
     * </p>
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
     * Navigates to the specified page route.
     * <p>
     * This method creates an instance of the page, initializes it with the primary stage, and shows the page.
     * It also updates the current and previous pages for navigation history.
     * </p>
     *
     * @param page The {@link MyPages} enum value representing the page to navigate to.
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
            previousPage = currentPage;
            currentPage = page;
            pageInstance.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the previously visited page.
     *
     * @return The {@link MyPages} enum value representing the previous page, or {@code null} if no previous page exists.
     */
    public MyPages getPreviousPage() {
        return previousPage;
    }

    /**
     * Returns the currently displayed page.
     *
     * @return The {@link MyPages} enum value representing the current page.
     */
    public MyPages getCurrentPage() {
        return currentPage;
    }
}