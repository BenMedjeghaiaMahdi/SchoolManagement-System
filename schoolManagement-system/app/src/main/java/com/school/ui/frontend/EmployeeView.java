package com.school.ui.frontend;

import com.school.backend.service.SchoolService;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class EmployeeView {

    private Stage stage;
    private final SchoolService schoolService;
    public EmployeeView(Stage stage, SchoolService schoolService) {
        this.stage = stage;
        this.schoolService = schoolService;
    }

    public void show() {
        Label label = new Label("Welcome Employé Test  Page");
        label.setStyle("-fx-font-size:24px;");

        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 600, 400);

        stage.setScene(scene);
    }
}
