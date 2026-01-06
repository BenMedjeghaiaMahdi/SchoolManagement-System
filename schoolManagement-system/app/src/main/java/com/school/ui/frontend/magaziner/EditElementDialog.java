package com.school.ui.frontend.magaziner;

import com.school.backend.model.Material;
import com.school.backend.model.MaterialLog;
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
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class EditElementDialog {

    private static final Logger LOGGER = Logger.getLogger(EditElementDialog.class.getName());

    private final Stage stage;
    private final int elementId;
    private File selectedImageFile;

    private final ObservableList<Map<String, String>> historyData =
            FXCollections.observableArrayList();

    public EditElementDialog(Stage owner, int elementId) {
        this.elementId = elementId;

        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Edit Element");

        Scene scene = new Scene(createRoot(), 600, 360);
        scene.getStylesheets().add(
                getClass().getResource(
                        "/com/school/ui/frontend/css/AddElementDialog-style.css"
                ).toExternalForm()
        );
        stage.setScene(scene);
    }

    private Parent createRoot() {

        // ================= IMAGE =================
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(240);
        imageView.setPreserveRatio(true);

        Label imageHint = new Label("Click to change image");
        imageHint.getStyleClass().add("image-hint");

        StackPane imageBox = new StackPane(imageView, imageHint);
        imageBox.setPrefWidth(220);
        imageBox.getStyleClass().add("image-box");

        imageBox.setOnMouseClicked(e ->
                chooseImage(imageView, imageHint)
        );

        // ================= FORM =================
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        TextField nameField = createField();
        TextField modelField = createField();
        TextField categoryField = createField();
        TextField quantityField = createField();

        ComboBox<String> stateBox = new ComboBox<>();
        stateBox.getItems().addAll("New", "Good", "Used", "Damaged");
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

        table.setEditable(false);

        table.getColumns().addAll(
                column("ID", "id", 40),
                column("Action", "action", 80),
                column("Change", "change", 60),
                column("Before", "before", 70),
                column("After", "after", 70),
                column("Date", "date", 150)
        );

        // ================= BUTTONS =================
        Button modifyBtn = new Button("Save");
        modifyBtn.getStyleClass().add("dialog-btn-primary");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("dialog-btn-secondary");

        modifyBtn.setOnAction(e ->
                handleSave(nameField, modelField, categoryField, quantityField, stateBox)
        );

        cancelBtn.setOnAction(e -> stage.close());

        HBox buttons = new HBox(10, modifyBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10));

        // ================= ROOT =================
        BorderPane root = new BorderPane();
        root.getStyleClass().add("add-dialog-root");
        root.setTop(top);
        root.setCenter(table);
        root.setBottom(buttons);

        BorderPane.setMargin(table, new Insets(10));

        // ================= LOAD DATA =================
        loadElementFromDatabase(
                nameField,
                modelField,
                categoryField,
                quantityField,
                stateBox,
                imageView,
                imageHint
        );

        loadHistoryFromDatabase();

        return root;
    }

    // ================= DATABASE OPERATIONS =================

    private void loadElementFromDatabase(
            TextField name,
            TextField model,
            TextField category,
            TextField quantity,
            ComboBox<String> state,
            ImageView imageView,
            Label imageHint
    ) {
        try {
            LOGGER.fine("Loading material ID: " + elementId);

            Material material = SchoolService.getInstance().getMaterial(elementId);

            if (material != null) {
                name.setText(material.getName());
                model.setText(material.getModel());
                category.setText(material.getCategory());
                quantity.setText(String.valueOf(material.getQuantity()));
                state.setValue(material.getStatus());

                if (material.getPhotoPath() != null && !material.getPhotoPath().isEmpty()) {
                    try {
                        // Convert backslashes to forward slashes for cross-platform compatibility
                        String imagePath = material.getPhotoPath().replace("\\", "/");
                        Image img = new Image("file:///" + imagePath);
                        imageView.setImage(img);
                        imageHint.setVisible(false);
                        LOGGER.fine("Image loaded: " + imagePath);
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to load image: " + material.getPhotoPath(), e);
                    }
                }
            }
        } catch (SecurityException se) {
            showError("Permission Denied", se.getMessage());
            stage.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load material", e);
            showError("Error", "Failed to load material: " + e.getMessage());
            stage.close();
        }
    }

    private void loadHistoryFromDatabase() {
        try {
            LOGGER.fine("Loading history for material ID: " + elementId);

            List<MaterialLog> logs = SchoolService.getInstance().getMaterialHistory(elementId);

            for (MaterialLog log : logs) {
                Map<String, String> row = new HashMap<>();
                row.put("id", String.valueOf(log.getId()));
                row.put("action", log.getAction());
                row.put("change", String.valueOf(log.getQuantityChange()));
                row.put("before", String.valueOf(log.getQuantityBefore()));
                row.put("after", String.valueOf(log.getQuantityAfter()));
                row.put("date", log.getLogDate() != null ? log.getLogDate().toString() : "N/A");
                historyData.add(row);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load history", e);
        }
    }

    private void handleSave(
            TextField nameField,
            TextField modelField,
            TextField categoryField,
            TextField quantityField,
            ComboBox<String> stateBox
    ) {
        try {
            // Validate inputs
            if (!validateInputs(nameField, modelField, categoryField, quantityField, stateBox)) {
                return;
            }

            // Create material object
            Material material = new Material();
            material.setId(elementId);
            material.setName(nameField.getText().trim());
            material.setModel(modelField.getText().trim());
            material.setCategory(categoryField.getText().trim());
            material.setQuantity(Integer.parseInt(quantityField.getText().trim()));
            material.setStatus(stateBox.getValue());

            LOGGER.info("Saving material ID: " + elementId);

            // Get existing material to preserve photo path if not changing image
            Material existingMaterial = SchoolService.getInstance().getMaterial(elementId);
            if (selectedImageFile == null && existingMaterial != null) {
                // Keep existing photo path if no new image selected
                material.setPhotoPath(existingMaterial.getPhotoPath());
            }

            // Update material in database
            SchoolService.getInstance().updateMaterial(material);

            // Update image if selected
            if (selectedImageFile != null) {
                try {
                    SchoolService.getInstance().updateMaterialPhoto(elementId, selectedImageFile);
                } catch (IllegalArgumentException iae) {
                    showError("Image Error", iae.getMessage());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Image update failed", e);
                    showError("Image Error", e.getMessage());
                }
            }

            showSuccess("Material saved successfully");
            stage.close();

        } catch (SecurityException se) {
            showError("Permission Denied", se.getMessage());
        } catch (IllegalArgumentException iae) {
            showError("Validation Error", iae.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to save material", e);
            showError("Error", "Failed to save material: " + e.getMessage());
        }
    }

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

        if (name.isEmpty()) {
            showError("Validation Error", "Name cannot be empty");
            nameField.requestFocus();
            return false;
        }

        if (model.isEmpty()) {
            showError("Validation Error", "Model cannot be empty");
            modelField.requestFocus();
            return false;
        }

        if (category.isEmpty()) {
            showError("Validation Error", "Category cannot be empty");
            categoryField.requestFocus();
            return false;
        }

        if (quantityStr.isEmpty()) {
            showError("Validation Error", "Quantity cannot be empty");
            quantityField.requestFocus();
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                showError("Validation Error", "Quantity cannot be negative");
                quantityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Validation Error", "Quantity must be a valid number");
            quantityField.requestFocus();
            return false;
        }

        if (state == null || state.isEmpty()) {
            showError("Validation Error", "Please select a state");
            return false;
        }

        return true;
    }

    // ================= HELPERS =================

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
                selectedImageFile = file;
                view.setImage(new Image(file.toURI().toString()));
                hint.setVisible(false);
                LOGGER.fine("Image selected: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to choose image", e);
            showError("Image Error", "Failed to load image");
        }
    }

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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}