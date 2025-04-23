package application.pages.admin;

import application.framework.*;
import database.model.entities.Announcement;
import database.model.entities.Message;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import utils.permissions.Roles;

import java.util.List;

/**
 * Displays all announcements from latest to oldest for all users,
 * and staff may create, edit, or delete an announcement.
 *
 * @author Tyler
 */
@Route(MyPages.ANNOUNCEMENTS)
@View(title = "Announcements")
public class AnnouncementsPage extends BasePage {
    // ListView to display announcements
    private final ListView<Pair<Integer, VBox>> announcementView = new ListView<>();

    /**
     * Creates the layout for the AnnouncementsPage
     *
     * @return layout
     */
    @Override
    public Pane createView() {
        VBox layout = new VBox(15);
        layout.setStyle(DesignGuide.MAIN_PADDING + " " + DesignGuide.CENTER_ALIGN);

        // Label for the title of the page
        Label titleLabel = UIFactory.createLabel("Announcements");
        titleLabel.getStyleClass().add("heading");
        layout.getChildren().add(titleLabel);

        // HBox for staff buttons
        HBox staffButtons = new HBox(10);

        if (context.getSession().getCurrentRole() == Roles.STAFF) {
            // Button to create an announcement
            Button createAnnouncementButton = UIFactory.createButton("Create", e -> e.onAction(a ->
                    createAnnouncementWindow()));

            // Button to edit an announcement
            Button editAnnouncementButton = UIFactory.createButton("Edit", e -> e.onAction(a -> {
                Pair<Integer, VBox> selectedAnnouncement = announcementView.getSelectionModel().getSelectedItem();
                if (selectedAnnouncement != null) {
                    editAnnouncementWindow(selectedAnnouncement.getKey());
                }
            }));

            // Button to delete an announcement
            Button deleteAnnouncementButton = UIFactory.createButton("Delete", e -> e.onAction(a ->
                    deleteAnnouncement()));

            staffButtons.getChildren().addAll(createAnnouncementButton, editAnnouncementButton, deleteAnnouncementButton);
            layout.getChildren().add(staffButtons);
        }

        // Set up announcementView
        announcementView.setFixedCellSize(50);
        announcementView.setPlaceholder(UIFactory.createLabel("No Announcements"));
        announcementView.setCellFactory(lv -> new ListCell<Pair<Integer, VBox>>() {
            @Override
            protected void updateItem(Pair<Integer, VBox> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item.getValue());
                }
            }
        });

        loadAnnouncements();

        // Button to route back to the home page
        Button backButton = UIFactory.createBackButton(context);

        layout.getChildren().addAll(announcementView, backButton);
        return layout;
    }

    /**
     * Loads all announcements from the database into the announcementView
     * sorted by newest to oldest
     */
    private void loadAnnouncements() {
        announcementView.getItems().clear();
        List<Announcement> announcements = context.announcements().getAll();
        for (Announcement announcement : announcements) {
            announcementView.getItems().addFirst(new Pair<>(announcement.getId(), createAnnouncementVBox(announcement)));
        }
    }

    /**
     * Creates a VBox containing the title and content of the given announcement
     *
     * @param announcement the {@link Announcement} to create a VBox for
     * @return the VBox containing the announcement information
     */
    private VBox createAnnouncementVBox(Announcement announcement) {
        // Label for the announcement's title
        Label titleLabel = new Label(announcement.getTitle() + " | " + announcement.getMessage().getCreatedAt().toString());
        titleLabel.setStyle("-fx-font-weight: bold");

        // Label for the announcement's content
        Label contentLabel = new Label(announcement.getMessage().getContent());

        return new VBox(titleLabel, contentLabel);
    }

    /**
     * Opens a window for creating a new announcement
     */
    private void createAnnouncementWindow() {
        Stage createStage = new Stage();
        createStage.initModality(Modality.APPLICATION_MODAL);

        // Labels for creating title and content
        Label AnnouncementTitle = UIFactory.createLabel("Announcement Title");
        Label AnnouncementContent = UIFactory.createLabel("Announcement Content");

        // TextFields for the user to input the title and content
        TextField titleField = UIFactory.createTextField("Enter Title");
        TextField contentField = UIFactory.createTextField("Enter Content");

        // Button to create the announcement
        Button createButton = UIFactory.createButton("Create", e -> e.onAction(a -> {
            String Content = contentField.getText();
            String Title = titleField.getText();
            Message m = new Message(context.getSession().getActiveUser().getId(), Content);
            Announcement announcement = new Announcement(m, Title);
            context.announcements().create(announcement);
            loadAnnouncements(); // Refresh the announcementView
            createStage.close();
        }));

        // Button to cancel and close the create window
        Button cancelButton = UIFactory.createButton("Cancel", e -> e.onAction(
                a -> {
                    titleField.clear();
                    contentField.clear();
                    createStage.close();
                }));

        VBox createLayout = new VBox(10, AnnouncementTitle, titleField, AnnouncementContent, contentField, createButton, cancelButton);
        createStage.setScene(new Scene(createLayout, 400, 200));
        createStage.show();
    }

    /**
     * Opens a window for editing the selected Announcement's title and content.
     *
     * @param announcementId The ID of the Announcement to edit.
     */
    private void editAnnouncementWindow(int announcementId) {
        Announcement announcement = context.announcements().getById(announcementId);
        if (announcement.getMessage().getUserId() != context.getSession().getActiveUser().getId()) {
            return;
        }
        Stage editorStage = new Stage();
        editorStage.initModality(Modality.APPLICATION_MODAL);

        // Labels for editing title and content
        Label editAnnouncementTitle = UIFactory.createLabel("Edit Announcement Title");
        Label editAnnouncementContent = UIFactory.createLabel("Edit Announcement Content");

        String currentTitle = announcement.getTitle().trim();
        String currentContent = announcement.getMessage().getContent().trim();

        TextField editAnnouncementTitleField = UIFactory.createTextField("New title", f -> f.defaultText(currentTitle));
        TextField editAnnouncementField = UIFactory.createTextField("New content", f -> f.defaultText(currentContent));

        // Save button to save the updated content
        Button saveButton = UIFactory.createButton("Save", e -> e.onAction(a -> {
            String newContent = editAnnouncementField.getText();
            String newTitle = editAnnouncementTitleField.getText();
            Announcement updatedAnnouncement = context.announcements().getById(announcementId);
            updatedAnnouncement.setTitle(newTitle);
            updatedAnnouncement.getMessage().setContent(newContent);
            context.announcements().update(updatedAnnouncement);
            loadAnnouncements(); // Refresh the announcementView
            editorStage.close();
        }));

        // Cancel button to close the edit window
        Button cancelButton = UIFactory.createButton("Cancel", e -> e.onAction(
                a -> {
                    editAnnouncementTitleField.clear();
                    editAnnouncementField.clear();
                    editorStage.close();
                }));

        VBox editorLayout = new VBox(10, editAnnouncementTitle, editAnnouncementTitleField, editAnnouncementContent, editAnnouncementField, saveButton, cancelButton);
        editorStage.setScene(new Scene(editorLayout, 400, 200));
        editorStage.show();
    }

    /**
     * Deletes an announcement from the announcementView and calls
     * a method to delete it from the database
     */
    private void deleteAnnouncement() {
        Pair<Integer, VBox> selectedItem = announcementView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            Announcement a = context.announcements().getById(selectedItem.getKey());
            // Return if not the owner of the announcement
            if (a.getMessage().getUserId() != context.getSession().getActiveUser().getId()) {
                return;
            }
            context.announcements().delete(selectedItem.getKey());
            announcementView.getItems().remove(selectedItem);
        }
    }
}