package com.school.ui.frontend.magaziner;

import com.school.backend.model.Material;
import com.school.backend.service.SchoolService;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.geometry.*;

import javafx.collections.*;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Dialog for adding new materials to inventory.
 * Includes form validation, image upload, and database persistence.
 */
public class AddElementDialog {

    private static final Logger LOGGER = Logger.getLogger(AddElementDialog.class.getName());
    private final Stage stage;
    private final ObservableList<Map<String, String>> historyData =
            FXCollections.observableArrayList();
    private File selectedImage;

    // Constants
    private static final int IMAGE_WIDTH = 180;
    private static final int IMAGE_HEIGHT = 240;
    private static final int DIALOG_WIDTH = 600;
    private static final int DIALOG_HEIGHT = 360;
    private static final int FORM_PADDING = 10;
    private static final int FORM_SPACING = 10;

    public AddElementDialog(Stage owner) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add Material");

        Scene scene = new Scene(createRoot(), DIALOG_WIDTH, DIALOG_HEIGHT);
        scene.getStylesheets().add(
                getClass().getResource("/com/school/ui/frontend/css/AddElementDialog-style.css").toExternalForm()
        );
        stage.setScene(scene);
    }

    private Parent createRoot() {

        // ================= IMAGE =================
        ImageView imageView = new ImageView();
        imageView.setFitWidth(IMAGE_WIDTH);
        imageView.setFitHeight(IMAGE_HEIGHT);
        imageView.setPreserveRatio(true);

        Label imageHint = new Label("Click to choose image");
        imageHint.getStyleClass().add("image-hint");

        StackPane imageBox = new StackPane(imageView, imageHint);
        imageBox.setPrefWidth(220);
        imageBox.getStyleClass().add("image-box");

        imageBox.setOnMouseClicked(e -> chooseImage(imageView, imageHint));

        // ================= FORM =================
        GridPane form = new GridPane();
        form.setHgap(FORM_SPACING);
        form.setVgap(FORM_SPACING);

        TextField nameField = createField();
        TextField modelField = createField();
        TextField categoryField = createField();
        TextField quantityField = createField();

        ComboBox<String> stateBox = new ComboBox<>();
        stateBox.getItems().addAll("New", "Good", "Used", "Damaged");
        stateBox.setValue("Good");
        stateBox.getStyleClass().add("form-field");

        form.addRow(0, label("Name:"), nameField);
        form.addRow(1, label("Model:"), modelField);
        form.addRow(2, label("Category:"), categoryField);
        form.addRow(3, label("Quantity:"), quantityField);
        form.addRow(4, label("State:"), stateBox);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(30);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(70);
        form.getColumnConstraints().addAll(c1, c2);

        VBox formBox = new VBox(form);

        // ================= TOP =================
        HBox top = new HBox(12, imageBox, formBox);
        top.getStyleClass().add("add-dialog-top");

        // ================= TABLE =================
        TableView<Map<String, String>> table =
                new TableView<>(historyData);
        table.getStyleClass().add("add-dialog-table");

        table.getColumns().addAll(
                column("ID", "id", 40),
                column("Name", "name", 120),
                column("Category", "category", 120),
                column("Quantity", "quantity", 80),
                column("State", "state", 100)
        );

        // ================= BUTTONS =================
        Button addBtn = new Button("Add Material");
        addBtn.getStyleClass().add("dialog-btn-primary");
        addBtn.setStyle("-fx-padding: 10px 30px;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("dialog-btn-secondary");
        cancelBtn.setStyle("-fx-padding: 10px 30px;");

        cancelBtn.setOnAction(e -> {
            LOGGER.info("Add material dialog cancelled");
            stage.close();
        });

        addBtn.setOnAction(e -> handleAddMaterial(
                nameField, modelField, categoryField, quantityField, stateBox, addBtn
        ));

        HBox buttons = new HBox(10, addBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(FORM_PADDING));

        // ================= ROOT =================
        BorderPane root = new BorderPane();
        root.getStyleClass().add("add-dialog-root");
        root.setTop(top);
        root.setCenter(table);
        root.setBottom(buttons);

        BorderPane.setMargin(table, new Insets(10));

        return root;
    }

    // ================= DATABASE OPERATIONS =================

    /**
     * Handles adding a new material with validation and image upload.
     */
    private void handleAddMaterial(
            TextField nameField,
            TextField modelField,
            TextField categoryField,
            TextField quantityField,
            ComboBox<String> stateBox,
            Button addBtn
    ) {
        try {
            // Validate inputs
            if (!validateInputs(nameField, modelField, categoryField, quantityField, stateBox)) {
                return;
            }

            // Disable button during save
            addBtn.setDisable(true);
            addBtn.setText("Adding...");

            // Get field values
            String name = nameField.getText().trim();
            String model = modelField.getText().trim();
            String category = categoryField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            String state = stateBox.getValue();

            // Create material object
            Material material = new Material();
            material.setName(name);
            material.setModel(model);
            material.setCategory(category);
            material.setQuantity(quantity);
            material.setStatus(state);

            LOGGER.info("Adding new material: " + name);

            // Save material to database
            Material savedMaterial = SchoolService.getInstance().addMaterial(material);

            // Upload image if selected
            if (selectedImage != null) {
                try {
                    SchoolService.getInstance().updateMaterialPhoto(
                            savedMaterial.getId(), selectedImage
                    );
                    LOGGER.info("Material image uploaded successfully");
                } catch (IllegalArgumentException iae) {
                    showError("Image Error", iae.getMessage());
                    addBtn.setDisable(false);
                    addBtn.setText("Add Material");
                    return;
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Image upload failed", e);
                    showError("Image Error", "Failed to upload image: " + e.getMessage());
                    // Continue - image upload failure shouldn't block material creation
                }
            }

            // Add to table display
            Map<String, String> row = new HashMap<>();
            row.put("id", String.valueOf(savedMaterial.getId()));
            row.put("name", savedMaterial.getName());
            row.put("category", savedMaterial.getCategory());
            row.put("quantity", String.valueOf(savedMaterial.getQuantity()));
            row.put("state", savedMaterial.getStatus());
            historyData.add(row);

            showSuccess("Material '" + name + "' added successfully");
            LOGGER.info("Material added successfully with ID: " + savedMaterial.getId());

            stage.close();

        } catch (SecurityException se) {
            LOGGER.log(Level.WARNING, "Permission denied", se);
            showError("Permission Denied", se.getMessage());
            addBtn.setDisable(false);
            addBtn.setText("Add Material");
        } catch (NumberFormatException nfe) {
            showError("Invalid Input", "Quantity must be a valid number");
            addBtn.setDisable(false);
            addBtn.setText("Add Material");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to add material", e);
            showError("Error", "Failed to add material: " + e.getMessage());
            addBtn.setDisable(false);
            addBtn.setText("Add Material");
        }
    }

    /**
     * Validates all form inputs before saving.
     */
    private boolean validateInputs(
            TextField nameField,
            TextField modelField,
            TextField categoryField,
            TextField quantityField,
            ComboBox<String> stateBox
    ) {
        String name = nameField.getText().trim();
        String model = modelField.getText().trim();
        String category = categoryField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        String state = stateBox.getValue();

        // Name validation
        if (name.isEmpty()) {
            showError("Validation Error", "Material name cannot be empty");
            nameField.requestFocus();
            LOGGER.warning("Validation failed: empty name");
            return false;
        }

        if (name.length() > 100) {
            showError("Validation Error", "Material name too long (max 100 characters)");
            nameField.requestFocus();
            return false;
        }

        // Model validation
        if (model.isEmpty()) {
            showError("Validation Error", "Material model cannot be empty");
            modelField.requestFocus();
            LOGGER.warning("Validation failed: empty model");
            return false;
        }

        if (model.length() > 100) {
            showError("Validation Error", "Material model too long (max 100 characters)");
            modelField.requestFocus();
            return false;
        }

        // Category validation
        if (category.isEmpty()) {
            showError("Validation Error", "Material category cannot be empty");
            categoryField.requestFocus();
            LOGGER.warning("Validation failed: empty category");
            return false;
        }

        // Quantity validation
        if (quantityStr.isEmpty()) {
            showError("Validation Error", "Quantity cannot be empty");
            quantityField.requestFocus();
            LOGGER.warning("Validation failed: empty quantity");
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                showError("Validation Error", "Quantity cannot be negative");
                quantityField.requestFocus();
                LOGGER.warning("Validation failed: negative quantity");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Validation Error", "Quantity must be a valid number");
            quantityField.requestFocus();
            LOGGER.warning("Validation failed: invalid quantity format");
            return false;
        }

        // State validation
        if (state == null || state.isEmpty()) {
            showError("Validation Error", "Please select a material state");
            LOGGER.warning("Validation failed: no state selected");
            return false;
        }

        return true;
    }

    // ================= UI HELPERS =================

    private Label label(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("form-label");
        return l;
    }

    private TextField createField() {
        TextField tf = new TextField();
        tf.getStyleClass().add("form-field");
        return tf;
    }

    private TableColumn<Map<String, String>, String> column(
            String title, String key, int width) {

        TableColumn<Map<String, String>, String> col =
                new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().get(key)));
        return col;
    }

    /**
     * Opens file chooser for image selection with validation.
     */
    private void chooseImage(ImageView view, Label hint) {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose Material Image");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Images", "*.png", "*.jpg", "*.jpeg", "*.gif"
                    )
            );
            File file = chooser.showOpenDialog(stage);

            if (file != null) {
                // Validate image file
                if (file.length() > 5 * 1024 * 1024) {
                    showError("Image Too Large", "Image size cannot exceed 5MB");
                    LOGGER.warning("Image too large: " + file.length() + " bytes");
                    return;
                }

                selectedImage = file;
                Image img = new Image(file.toURI().toString());

                if (img.isError()) {
                    showError("Image Error", "Failed to load image");
                    selectedImage = null;
                    LOGGER.warning("Failed to load image: " + file.getAbsolutePath());
                    return;
                }

                view.setImage(img);
                hint.setVisible(false);
                LOGGER.fine("Image selected: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to choose image", e);
            showError("Image Error", "Failed to load image: " + e.getMessage());
        }
    }

    // ================= DIALOGS =================

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
     * Shows success alert dialog.
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}