package com.school.ui.frontend;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class EconomyView {

    private Stage stage;

    public EconomyView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Label label = new Label("Welcome Employé Test  Page");
        label.setStyle("-fx-font-size:24px;");

        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 600, 400);

        stage.setScene(scene);
    }
}
