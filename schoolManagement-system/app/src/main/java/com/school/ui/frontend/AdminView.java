package com.school.ui.frontend;

import com.school.backend.service.SchoolService;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminView {

    private Stage stage;
    private final SchoolService schoolService;
    public AdminView(Stage stage, SchoolService schoolService) {
        this.stage = stage;
        this.schoolService = schoolService;
    }

    public void show() {
        Label label = new Label("Welcome "+ schoolService.getCurrentUser().getFirstName()+" "+schoolService.getCurrentUser().getLastName());
        label.setStyle("-fx-font-size:24px;");

        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 600, 400);

        stage.setScene(scene);
    }
}
