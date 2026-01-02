package com.school.ui.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginView {

    private Stage stage;

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {

        Label title = new Label("School Management System Test Page");
        title.setFont(Font.font("Arial", 26));
        title.setTextFill(Color.DARKBLUE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(200);
        loginBtn.setStyle("-fx-background-color:#2c7be5; -fx-text-fill:white;");

        Label message = new Label();
        message.setTextFill(Color.RED);

        loginBtn.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();


            if (user.equals("admin") && pass.equals("admin123")) {
                new AdminView(stage).show();
            }
            else if (user.equals("store") && pass.equals("store123")) {
                new MagasinierView(stage).show();
            }
            else if (user.equals("emp") && pass.equals("emp123")) {
                new EmployeView(stage).show();
            }
            else {
                message.setText("Username or Password incorrect!");
            }
        });

        VBox form = new VBox(15, title, usernameField, passwordField, loginBtn, message);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));
        form.setStyle(
                "-fx-background-color:white;" +
                        "-fx-border-radius:10;" +
                        "-fx-background-radius:10;"
        );

        StackPane root = new StackPane(form);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #74ebd5, #ACB6E5);");

        Scene scene = new Scene(root, 450, 500);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}
