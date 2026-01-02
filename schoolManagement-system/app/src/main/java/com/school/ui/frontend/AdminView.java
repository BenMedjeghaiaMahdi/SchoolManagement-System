package com.school.ui.frontend;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminView {

    private Stage stage;

    public AdminView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Label label = new Label("Welcome Admin Test page");
        label.setStyle("-fx-font-size:24px;");

        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 600, 400);

        stage.setScene(scene);
    }
}
