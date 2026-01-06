package com.school.ui.frontend.magaziner;

import com.school.backend.model.Material;
import com.school.backend.model.User;
import com.school.backend.service.SchoolService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Main view for magazine (materials inventory) management.
 * Displays materials table with search, filter, and CRUD operations.
 */
public class MagazinerView {

    private Stage stage;
    private final SchoolService schoolService;
    private FilterMode currentFilterMode = FilterMode.NAME;
    private static final Logger LOGGER = Logger.getLogger(MagazinerView.class.getName());

    public MagazinerView(Stage stage, SchoolService schoolService) {
        this.stage = stage;
        this.schoolService = schoolService;
    }

    public void show() {
        try {
            BorderPane root = new BorderPane();
            Image bgImage = new Image(
                    getClass().getResource("/com/school/ui/frontend/img/magazine-bg.png").toExternalForm()
            );
            ImageView bgView = new ImageView(bgImage);
            bgView.setFitWidth(1200);
            bgView.setFitHeight(700);
            root.getChildren().add(bgView);

            /* ================= HEADER ================= */
            HBox header = createHeader();
            BorderPane.setMargin(header, new Insets(20, 40, 0, 40));

            /* ================= TITLE ================= */
            Label title = new Label("Magazine");
            title.getStyleClass().add("page-title");
            HBox titleBox = new HBox(title);
            titleBox.setAlignment(Pos.CENTER);

            /* ================= LOAD TABLE DATA ================= */
            ObservableList<MaterialDto> materialData = FXCollections.observableArrayList();
            refreshTableData(materialData);

            /* ================= TABLE ================= */
            TableView<MaterialDto> table = new TableView<>();
            table.getStyleClass().add("magazine-table");
            setupTableColumns(table);

            // Setup filtering and sorting
            FilteredList<MaterialDto> filteredData = new FilteredList<>(materialData, p -> true);
            SortedList<MaterialDto> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(sortedData);

            /* ================= SEARCH + FILTER ================= */
            MenuButton filterMenu = new MenuButton("Filter: Name");
            MenuItem idItem = new MenuItem("ID");
            MenuItem nameItem = new MenuItem("Name");
            MenuItem categoryItem = new MenuItem("Category");

            idItem.setOnAction(e -> {
                currentFilterMode = FilterMode.ID;
                filterMenu.setText("Filter: ID");
            });

            nameItem.setOnAction(e -> {
                currentFilterMode = FilterMode.NAME;
                filterMenu.setText("Filter: Name");
            });

            categoryItem.setOnAction(e -> {
                currentFilterMode = FilterMode.CATEGORY;
                filterMenu.setText("Filter: Category");
            });

            filterMenu.getItems().addAll(idItem, nameItem, categoryItem);
            filterMenu.getStyleClass().add("filter-menu");

            TextField searchField = new TextField();
            searchField.setPromptText("Search...");
            searchField.getStyleClass().add("search-field");
            searchField.textProperty().addListener((obs, oldVal, newVal) ->
                    updateTableFilter(filteredData, newVal, currentFilterMode)
            );

            HBox searchBar = new HBox(10, filterMenu, searchField);
            searchBar.setAlignment(Pos.CENTER_RIGHT);

            /* ================= ACTION BUTTONS ================= */
            Button reserveBtn = new Button("Reserve");
            reserveBtn.getStyleClass().add("reserve-btn");
            reserveBtn.setOnAction(e -> handleReserve(table));

            Button deleteBtn = new Button("Delete");
            deleteBtn.getStyleClass().add("delete-btn");
            deleteBtn.setOnAction(e -> handleDelete(table, materialData));

            HBox actionBtns = new HBox(10, reserveBtn, deleteBtn);
            actionBtns.setAlignment(Pos.CENTER_LEFT);

            BorderPane toolsBar = new BorderPane();
            toolsBar.setLeft(actionBtns);
            toolsBar.setRight(searchBar);

            /* ================= SIDE ACTION BUTTONS ================= */
            Button addBtn = createIconButton(
                    "/com/school/ui/frontend/img/add-element.png",
                    "Add Material",
                    e -> handleAdd(materialData)
            );

            Button viewBtn = createIconButton(
                    "/com/school/ui/frontend/img/element-info.png",
                    "View Details",
                    e -> handleView(table)
            );

            Button editBtn = createIconButton(
                    "/com/school/ui/frontend/img/edit-info.png",
                    "Edit Material",
                    e -> handleEdit(table, materialData)
            );

            VBox sideActions = new VBox(15, addBtn, viewBtn, editBtn);
            sideActions.getStyleClass().add("side-actions");
            sideActions.setAlignment(Pos.TOP_CENTER);

            HBox tableSection = new HBox(15, sideActions, table);
            HBox.setHgrow(table, Priority.ALWAYS);

            VBox center = new VBox(20, titleBox, toolsBar, tableSection);
            center.setPadding(new Insets(20, 20, 40, 20));

            root.setTop(header);
            root.setCenter(center);

            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(
                    getClass().getResource("/com/school/ui/frontend/css/magazine.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.show();

            LOGGER.info("Magazine view loaded successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize magazine view", e);
            showError("Initialization Error", "Failed to load magazine view: " + e.getMessage());
        }
    }

    /**
     * Creates the header with notification, date/time, and user info.
     * Loads user image from database or uses default if not available.
     */
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(10, 20, 10, 20));

        // Notification button
        ImageView notifIcon = new ImageView(
                getClass().getResource("/com/school/ui/frontend/img/notification.png").toExternalForm()
        );
        notifIcon.setFitWidth(20);
        notifIcon.setFitHeight(20);

        Button notificationBtn = new Button();
        notificationBtn.setGraphic(notifIcon);
        notificationBtn.getStyleClass().add("icon-btn");
        notificationBtn.setOnAction(e -> showAlert("Notifications", "No new notifications"));

        // Date & Time
        Label dateTime = new Label();
        dateTime.getStyleClass().add("date-time");

        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy  HH:mm:ss");
                    dateTime.setText(LocalDateTime.now().format(formatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        // User image - Load from database or use default
        ImageView userImg = new ImageView();
        userImg.setFitWidth(32);
        userImg.setFitHeight(32);
        userImg.setPreserveRatio(true);

        User currentUser = schoolService.getCurrentUser();
        if (currentUser != null && currentUser.getPhotoPath() != null &&
                !currentUser.getPhotoPath().isEmpty()) {
            try {
                // Load user image from database path
                java.nio.file.Path absPath = java.nio.file.Paths.get(currentUser.getPhotoPath()).toAbsolutePath();

                if (java.nio.file.Files.exists(absPath)) {
                    String fileUri = absPath.toUri().toString();
                    Image userImage = new Image(fileUri);

                    if (!userImage.isError() && userImage.getWidth() > 0) {
                        userImg.setImage(userImage);
                        LOGGER.fine("User image loaded from database");
                    } else {
                        loadDefaultUserImage(userImg);
                    }
                } else {
                    LOGGER.warning("User image file not found: " + absPath);
                    loadDefaultUserImage(userImg);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load user image from database", e);
                loadDefaultUserImage(userImg);
            }
        } else {
            // Load default image if no photo path
            loadDefaultUserImage(userImg);
        }

        // User name
        String userName = currentUser != null ?
                currentUser.getFirstName() + " " + currentUser.getLastName() : "Unknown";
        Label userNameLabel = new Label(userName);
        userNameLabel.getStyleClass().add("user-name");

        HBox userBox = new HBox(10, userImg, userNameLabel);
        userBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        header.getChildren().addAll(notificationBtn, spacer1, dateTime, spacer2, userBox);
        return header;
    }

    /**
     * Loads default user image from resources.
     */
    private void loadDefaultUserImage(ImageView userImg) {
        try {
            Image defaultImage = new Image(
                    getClass().getResource("/com/school/ui/frontend/img/user.png").toExternalForm()
            );
            userImg.setImage(defaultImage);
            LOGGER.fine("Default user image loaded");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load default user image", e);
            // Image will remain empty/gray
        }
    }

    /**
     * Creates an icon button with tooltip.
     */
    private Button createIconButton(String iconPath, String tooltip, javafx.event.EventHandler handler) {
        Button btn = new Button();
        ImageView icon = new ImageView(new Image(getClass().getResource(iconPath).toExternalForm()));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        btn.setGraphic(icon);
        btn.getStyleClass().add("icon-btn");
        btn.setOnAction(handler);

        Tooltip tip = new Tooltip(tooltip);
        Tooltip.install(btn, tip);

        return btn;
    }

    /* ================= TABLE SETUP ================= */

    /**
     * Configures table columns with proper bindings.
     */
    private void setupTableColumns(TableView<MaterialDto> table) {
        TableColumn<MaterialDto, Boolean> selectCol = new TableColumn<>("✓");
        selectCol.setPrefWidth(40);
        selectCol.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));

        TableColumn<MaterialDto, Integer> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(60);
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<MaterialDto, String> nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(120);
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<MaterialDto, String> modelCol = new TableColumn<>("Model");
        modelCol.setPrefWidth(100);
        modelCol.setCellValueFactory(cellData -> cellData.getValue().modelProperty());

        TableColumn<MaterialDto, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setPrefWidth(80);
        quantityCol.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());

        TableColumn<MaterialDto, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setPrefWidth(120);
        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        TableColumn<MaterialDto, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        table.getColumns().addAll(selectCol, idCol, nameCol, modelCol, quantityCol, categoryCol, statusCol);
    }

    /* ================= FILTERING ================= */

    /**
     * Updates table filter based on search text and current filter mode.
     */
    private void updateTableFilter(FilteredList<MaterialDto> list, String searchText, FilterMode mode) {
        if (searchText == null || searchText.isEmpty()) {
            list.setPredicate(p -> true);
            return;
        }

        String lowerSearch = searchText.toLowerCase();
        list.setPredicate(item -> {
            switch (mode) {
                case ID:
                    return String.valueOf(item.idProperty().get()).contains(lowerSearch);
                case NAME:
                    return item.nameProperty().get().toLowerCase().contains(lowerSearch);
                case CATEGORY:
                    return item.categoryProperty().get().toLowerCase().contains(lowerSearch);
                default:
                    return true;
            }
        });
    }

    /* ================= DATA OPERATIONS ================= */

    /**
     * Reloads table data from database.
     */
    private void refreshTableData(ObservableList<MaterialDto> data) {
        try {
            data.clear();
            List<Material> materials = schoolService.getAllMaterials();
            data.addAll(materials.stream()
                    .map(MaterialDto::new)
                    .collect(Collectors.toList()));
            LOGGER.fine("Table refreshed: " + materials.size() + " materials");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to refresh table", e);
            showError("Load Error", "Failed to load materials: " + e.getMessage());
        }
    }

    /**
     * Gets selected material from table with validation.
     */
    private MaterialDto getSelectedMaterial(TableView<MaterialDto> table) {
        MaterialDto selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Required", "Please select a material first");
            return null;
        }
        return selected;
    }

    /* ================= EVENT HANDLERS ================= */

    /**
     * Handles add material action.
     */
    private void handleAdd(ObservableList<MaterialDto> data) {
        try {
            AddElementDialog addDialog = new AddElementDialog(stage);
            addDialog.showAndWait();
            refreshTableData(data);
            LOGGER.info("Add dialog closed, table refreshed");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to open add dialog", e);
            showError("Dialog Error", "Failed to open add dialog: " + e.getMessage());
        }
    }

    /**
     * Handles edit material action.
     */
    private void handleEdit(TableView<MaterialDto> table, ObservableList<MaterialDto> data) {
        try {
            MaterialDto selected = getSelectedMaterial(table);
            if (selected != null) {
                int materialId = selected.idProperty().get();
                LOGGER.fine("Opening edit dialog for material: " + materialId);
                EditElementDialog editDialog = new EditElementDialog(stage, materialId);
                editDialog.showAndWait();
                refreshTableData(data);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to open edit dialog", e);
            showError("Dialog Error", "Failed to open edit dialog: " + e.getMessage());
        }
    }

    /**
     * Handles view material details action.
     */
    private void handleView(TableView<MaterialDto> table) {
        try {
            MaterialDto selected = getSelectedMaterial(table);
            if (selected != null) {
                int materialId = selected.idProperty().get();
                LOGGER.fine("Opening view dialog for material: " + materialId);
                ViewElementDialog viewDialog = new ViewElementDialog(stage, materialId);
                viewDialog.showAndWait();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to open view dialog", e);
            showError("Dialog Error", "Failed to open view dialog: " + e.getMessage());
        }
    }

    /**
     * Handles delete material action with confirmation.
     */
    private void handleDelete(TableView<MaterialDto> table, ObservableList<MaterialDto> data) {
        try {
            MaterialDto selected = getSelectedMaterial(table);
            if (selected != null) {
                String materialName = selected.nameProperty().get();
                if (confirmDelete("Delete '" + materialName + "'? This cannot be undone.")) {
                    int materialId = selected.idProperty().get();
                    LOGGER.info("Deleting material ID: " + materialId);
                    schoolService.deleteMaterial(materialId);
                    data.remove(selected);
                    showSuccess("Material deleted successfully");
                }
            }
        } catch (SecurityException se) {
            LOGGER.log(Level.WARNING, "Permission denied for delete", se);
            showError("Permission Denied", se.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to delete material", e);
            showError("Delete Error", "Failed to delete material: " + e.getMessage());
        }
    }

    /**
     * Handles reserve material action for selected items.
     */
    private void handleReserve(TableView<MaterialDto> table) {
        try {
            ObservableList<MaterialDto> selected = table.getItems()
                    .filtered(item -> item.selectedProperty().get());

            if (selected.isEmpty()) {
                showAlert("Selection Required", "Please select at least one material to reserve");
                return;
            }

            LOGGER.fine("Reserve action for " + selected.size() + " materials");
            showAlert("Info", "Reservation feature coming soon (selected: " + selected.size() + " items)");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to handle reservation", e);
            showError("Reservation Error", "Failed to process reservation: " + e.getMessage());
        }
    }

    /* ================= DIALOGS ================= */

    /**
     * Shows confirmation dialog for delete operation.
     */
    private boolean confirmDelete(String message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        confirm.setContentText(message);
        return confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Shows error alert dialog.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows warning alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows success alert dialog.
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Filter mode enum for search/filter functionality.
     */
    enum FilterMode {
        ID, NAME, CATEGORY
    }
}

/**
 * Data Transfer Object for Material with JavaFX properties.
 * Used for table binding and selection management.
 */
class MaterialDto {

    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty model = new SimpleStringProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public MaterialDto(Material material) {
        this.id.set(material.getId());
        this.name.set(material.getName());
        this.model.set(material.getModel());
        this.quantity.set(material.getQuantity());
        this.category.set(material.getCategory());
        this.status.set(material.getStatus());
    }

    // Properties
    public BooleanProperty selectedProperty() { return selected; }
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty modelProperty() { return model; }
    public IntegerProperty quantityProperty() { return quantity; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty statusProperty() { return status; }

    // Getters
    public boolean isSelected() { return selected.get(); }
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getModel() { return model.get(); }
    public int getQuantity() { return quantity.get(); }
    public String getCategory() { return category.get(); }
    public String getStatus() { return status.get(); }

    // Setters
    public void setSelected(boolean value) { selected.set(value); }
    public void setId(int value) { id.set(value); }
    public void setName(String value) { name.set(value); }
    public void setModel(String value) { model.set(value); }
    public void setQuantity(int value) { quantity.set(value); }
    public void setCategory(String value) { category.set(value); }
    public void setStatus(String value) { status.set(value); }
}