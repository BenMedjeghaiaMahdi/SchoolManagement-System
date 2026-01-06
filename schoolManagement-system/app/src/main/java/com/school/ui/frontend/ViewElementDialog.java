package com.school.ui.frontend;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.geometry.*;

import javafx.collections.*;
import javafx.beans.property.SimpleStringProperty;

import java.util.*;

public class ViewElementDialog {

    private final Stage stage;

    // Table data (loaded from SQLite later)
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

        // imageBox is NOT clickable (read-only)

        // ================= FORM =================
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        TextField nameField = createField();
        TextField modelField = createField();
        TextField categoryField = createField();

        ComboBox<String> stateBox = new ComboBox<>();
        stateBox.getItems().addAll("New", "Good", "Used", "Damaged");
        stateBox.getStyleClass().add("form-field");

        // 🔒 READ ONLY
        nameField.setDisable(true);
        modelField.setDisable(true);
        categoryField.setDisable(true);
        stateBox.setDisable(true);

        form.addRow(0, label("Name:"), nameField);
        form.addRow(1, label("Model:"), modelField);
        form.addRow(2, label("Category:"), categoryField);
        form.addRow(3, label("State:"), stateBox);

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
                column("Name", "name", 120),
                column("Reserve Time", "reserveTime", 170),
                column("End Time", "endTime", 170)
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
                stateBox,
                imageView,
                imageHint
        );

        loadHistoryFromDatabase(elementId);

        return root;
    }

    // ================= DATABASE (COMMENTED) =================
    private void loadElementFromDatabase(
            int elementId,
            TextField name,
            TextField model,
            TextField category,
            ComboBox<String> state,
            ImageView imageView,
            Label imageHint
    ) {

        /*
        // SQLite connection
        Connection conn = DriverManager.getConnection("jdbc:sqlite:school.db");

        PreparedStatement ps = conn.prepareStatement(
            "SELECT name, model, category, state, image_path FROM elements WHERE id = ?"
        );
        ps.setInt(1, elementId);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            name.setText(rs.getString("name"));
            model.setText(rs.getString("model"));
            category.setText(rs.getString("category"));
            state.setValue(rs.getString("state"));

            String imagePath = rs.getString("image_path");
            if (imagePath != null) {
                imageView.setImage(new Image("file:" + imagePath));
                imageHint.setVisible(false);
            }
        }

        rs.close();
        ps.close();
        conn.close();
        */

        // ---- TEMP MOCK DATA ----
        name.setText("Laptop");
        model.setText("Dell 5420");
        category.setText("Electronics");
        state.setValue("Good");
    }

    private void loadHistoryFromDatabase(int elementId) {

        /*
        Connection conn = DriverManager.getConnection("jdbc:sqlite:school.db");

        PreparedStatement ps = conn.prepareStatement(
            "SELECT id, name, reserve_time, end_time FROM reservations WHERE element_id = ?"
        );
        ps.setInt(1, elementId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Map<String, String> row = new HashMap<>();
            row.put("id", rs.getString("id"));
            row.put("name", rs.getString("name"));
            row.put("reserveTime", rs.getString("reserve_time"));
            row.put("endTime", rs.getString("end_time"));
            historyData.add(row);
        }

        rs.close();
        ps.close();
        conn.close();
        */

        // ---- TEMP MOCK DATA ----
        Map<String, String> r = new HashMap<>();
        r.put("id", "1");
        r.put("name", "Laptop");
        r.put("reserveTime", "2025-01-01 10:00");
        r.put("endTime", "2025-01-05 12:00");
        historyData.add(r);
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

    public void showAndWait() {
        stage.showAndWait();
    }
}
