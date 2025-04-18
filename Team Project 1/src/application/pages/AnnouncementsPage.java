package application.pages;

import application.framework.*;
import database.model.entities.Announcement;
import database.model.entities.Message;
import database.model.entities.User;
import database.repository.repos.Announcements;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import utils.permissions.Roles;
import utils.permissions.RolesUtil;

import java.util.List;

/**
 * Displays all announcements in the system.
 * <p>
 * If the logged-in user has the STAFF role, this page allows them to create and delete announcements.
 * Non-staff users can only view the announcements.
 * </p>
 * <p>
 * Each announcement consists of a title and a message body.
 * </p>
 *
 * @author Dhruv
 * @see Announcements
 */
@Route(MyPages.ANNOUNCEMENTS)
@View(title = "Announcements")
public class AnnouncementsPage extends BasePage {

    private final Announcements announcementsRepo;
    private ListView<Announcement> announcementsList;
    private ObservableList<Announcement> announcementsData;

    private TextField titleField;
    private TextArea contentArea;

    /**
     * Constructs the {@code AnnouncementsPage} and initializes the announcements repository.
     */
    public AnnouncementsPage() {
        super();
        this.announcementsRepo = context.announcements();
    }

    /**
     * Builds and returns the layout for the Announcements page.
     * <p>
     * Staff users are shown additional fields and buttons to create or delete announcements.
     * </p>
     *
     * @return A {@link VBox} containing all UI elements.
     */
    @Override
    public VBox createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        Label pageTitle = UIFactory.createLabel("Announcements", f ->
                f.style("-fx-font-weight: bold; -fx-font-size: 16px;"));

        User currentUser = context.getSession().getActiveUser();
        boolean isStaff = (currentUser != null && RolesUtil.hasRole(currentUser.getRoles(), Roles.STAFF));

        announcementsList = new ListView<>();
        announcementsData = FXCollections.observableArrayList();
        announcementsList.setItems(announcementsData);
        announcementsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Announcement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + "  |  " + item.getMessage().getContent());
                }
            }
        });
        loadAnnouncements();

        if (isStaff) {
            titleField = UIFactory.createTextField("Announcement Title");
            contentArea = new TextArea();
            contentArea.setPromptText("Announcement Content");
            contentArea.setPrefRowCount(2);

            Button addButton = UIFactory.createButton("Add Announcement", b ->
                    b.onAction(e -> addAnnouncement()));
            Button deleteButton = UIFactory.createButton("Delete Selected", b ->
                    b.onAction(e -> deleteAnnouncement()));

            layout.getChildren().addAll(pageTitle, announcementsList, titleField, contentArea, addButton, deleteButton);
        } else {
            layout.getChildren().addAll(pageTitle, announcementsList);
        }

        Button backButton = UIFactory.createBackButton(context);
        layout.getChildren().add(backButton);

        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    /**
     * Loads all announcements from the database and populates the UI list.
     */
    private void loadAnnouncements() {
        announcementsData.clear();
        List<Announcement> all = announcementsRepo.getAll();
        announcementsData.addAll(all);
    }

    /**
     * Adds a new announcement using the provided title and message content.
     * <p>
     * The message will be associated with the current logged-in staff member.
     * </p>
     */
    private void addAnnouncement() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            UIFactory.showAlert(Alert.AlertType.WARNING, "Invalid Input", "Title and content cannot be empty.");
            return;
        }

        User currentUser = context.getSession().getActiveUser();
        if (currentUser == null) {
            UIFactory.showAlert(Alert.AlertType.ERROR, "Error", "No active user found.");
            return;
        }

        Message msg = new Message(currentUser.getId(), content);
        Announcement ann = new Announcement(msg, title);
        try {
            announcementsRepo.create(ann);
            loadAnnouncements();
            titleField.clear();
            contentArea.clear();
        } catch (Exception ex) {
            UIFactory.showAlert(Alert.AlertType.ERROR, "Error Creating Announcement", ex.getMessage());
        }
    }

    /**
     * Deletes the currently selected announcement from the database and refreshes the list.
     */
    private void deleteAnnouncement() {
        Announcement selected = announcementsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIFactory.showAlert(Alert.AlertType.INFORMATION, "No Selection", "Please select an announcement to delete.");
            return;
        }
        announcementsRepo.delete(selected.getId());
        loadAnnouncements();
    }
}