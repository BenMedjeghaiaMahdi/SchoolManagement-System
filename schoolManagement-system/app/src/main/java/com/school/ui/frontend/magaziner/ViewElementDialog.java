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

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ViewElementDialog {

    private static final Logger LOGGER = Logger.getLogger(ViewElementDialog.class.getName());
    private final Stage stage;

    // Table data (loaded from SQLite)
    private final ObservableList<Map<String, String>> historyData =
            FXCollections.observableArrayList();

    public ViewElementDialog(Stage owner, int elementId) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Element Details");

        Scene scene = new Scene(createRoot(elementId), 600, 360);
        scene.getStylesheets().add(
                getClass().getResource(
                        "/com/school/ui/frontend/css/AddElementDialog-style.css"
                ).toExternalForm()
        );
        stage.setScene(scene);
    }

    private Parent createRoot(int elementId) {

        // ================= IMAGE =================
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(240);
        imageView.setPreserveRatio(true);

        Label imageHint = new Label("No Image");
        imageHint.getStyleClass().add("image-hint");

        StackPane imageBox = new StackPane(imageView, imageHint);
        imageBox.setPrefWidth(220);
        imageBox.getStyleClass().add("image-box");

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

        // 🔒 READ ONLY - disable all fields
        nameField.setDisable(true);
        modelField.setDisable(true);
        categoryField.setDisable(true);
        quantityField.setDisable(true);
        stateBox.setDisable(true);

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

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setEditable(false);

        table.getColumns().addAll(
                column("ID", "id", 40),
                column("Action", "action", 80),
                column("Change", "change", 60),
                column("Before", "before", 70),
                column("After", "after", 70),
                column("Date", "date", 150)
        );

        // ================= BUTTON =================
        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("dialog-btn-secondary");
        closeBtn.setOnAction(e -> stage.close());

        HBox buttons = new HBox(closeBtn);
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
                elementId,
                nameField,
                modelField,
                categoryField,
                quantityField,
                stateBox,
                imageView,
                imageHint
        );

        loadHistoryFromDatabase(elementId);

        return root;
    }

    // ================= DATABASE OPERATIONS =================

    private void loadElementFromDatabase(
            int elementId,
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

                String photoPath = material.getPhotoPath();
                LOGGER.info("Photo path from DB: " + photoPath);

                if (photoPath != null && !photoPath.isEmpty()) {
                    try {
                        // Convert to absolute path
                        java.nio.file.Path absPath = java.nio.file.Paths.get(photoPath).toAbsolutePath();
                        LOGGER.info("Absolute path: " + absPath);

                        if (!java.nio.file.Files.exists(absPath)) {
                            LOGGER.warning("File does not exist: " + absPath);
                            imageHint.setText("Image file not found");
                        } else {
                            String fileUri = absPath.toUri().toString();
                            LOGGER.info("Loading image from: " + fileUri);

                            Image img = new Image(fileUri);

                            if (img.isError()) {
                                LOGGER.warning("Image loading error: " + img.getException());
                                imageHint.setText("Image failed to load");
                            } else if (img.getWidth() > 0) {
                                imageView.setImage(img);
                                imageHint.setVisible(false);
                                LOGGER.info("Image loaded successfully");
                            } else {
                                imageHint.setText("Invalid image format");
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to load image from path: " + photoPath, e);
                        imageHint.setText("Error loading image");
                    }
                } else {
                    imageHint.setText("No image available");
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

    private void loadHistoryFromDatabase(int elementId) {
        try {
            LOGGER.fine("Loading history for material ID: " + elementId);

            List<MaterialLog> logs = SchoolService.getInstance()
                    .getMaterialHistory(elementId);

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

    // ================= HELPERS =================

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

    public void showAndWait() {
        stage.showAndWait();
    }
}