package com.school.ui.frontend;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.geometry.*;

import javafx.collections.*;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AddElementDialog {

    private final Stage stage;
    private final ObservableList<Map<String, String>> historyData =
            FXCollections.observableArrayList();

    public AddElementDialog(Stage owner) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add Element");

        Scene scene = new Scene(createRoot(), 600, 360);
        scene.getStylesheets().add(
                getClass().getResource("/com/school/ui/frontend/css/AddElementDialog-style.css").toExternalForm()
        );
        stage.setScene(scene);
    }

    private Parent createRoot() {

        // ================= IMAGE =================
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(240);
        imageView.setPreserveRatio(true);

        Label imageHint = new Label("Click to choose image");
        imageHint.getStyleClass().add("image-hint");

        StackPane imageBox = new StackPane(imageView, imageHint);
        imageBox.setPrefWidth(220);
        imageBox.getStyleClass().add("image-box");

        imageBox.setOnMouseClicked(e -> chooseImage(imageView, imageHint));

        // ================= FORM =================
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        TextField nameField = createField();
        TextField modelField = createField();
        TextField categoryField = createField();

        ComboBox<String> stateBox = new ComboBox<>();
        stateBox.getItems().addAll("New", "Good", "Used", "Damaged");
        stateBox.setValue("Good");
        stateBox.getStyleClass().add("form-field");

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

        table.getColumns().addAll(
                column("ID", "id", 40),
                column("Name", "name", 120),
                column("Reserve Time", "reserveTime", 170),
                column("End Time", "endTime", 170)
        );

        // ================= BUTTONS =================
        Button addBtn = new Button("Add");
        addBtn.getStyleClass().add("dialog-btn-primary");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("dialog-btn-secondary");

        cancelBtn.setOnAction(e -> stage.close());

        HBox buttons = new HBox(10, addBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10));

        // ================= ROOT =================
        BorderPane root = new BorderPane();
        root.getStyleClass().add("add-dialog-root");
        root.setTop(top);
        root.setCenter(table);
        root.setBottom(buttons);

        BorderPane.setMargin(table, new Insets(10));

        return root;
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

    private void chooseImage(ImageView view, Label hint) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Images", "*.png", "*.jpg", "*.jpeg", "*.gif"
                )
        );
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            view.setImage(new Image(file.toURI().toString()));
            hint.setVisible(false);
        }
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}
