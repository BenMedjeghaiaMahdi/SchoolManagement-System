package com.school.ui.frontend;

import com.school.backend.model.User;
import com.school.backend.service.SchoolService;
import com.school.ui.frontend.magaziner.MagazinerView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView {

    private final SchoolService service = SchoolService.getInstance();
    private final Stage stage;

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {

        StackPane root = new StackPane();

        Image bgImage = new Image(
                getClass().getResource("/com/school/ui/frontend/img/background.jpg").toExternalForm()
        );
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(1200);
        bgView.setFitHeight(700);
        bgView.setPreserveRatio(false);


        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));
        form.setMaxWidth(420);
        form.getStyleClass().add("login-form");


        Label title = new Label("Login");
        title.getStyleClass().add("login-title");


        Label userNameLabel = new Label("Email :");
        userNameLabel.getStyleClass().add("form-label");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your Email");
        usernameField.getStyleClass().add("form-input");
        usernameField.setMaxWidth(Double.MAX_VALUE);

        VBox usernameBox = new VBox(5, userNameLabel, usernameField);
        usernameBox.setAlignment(Pos.CENTER_LEFT);


        Label passwordLabel = new Label("Password :");
        passwordLabel.getStyleClass().add("form-label");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("form-input");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        VBox passwordBox = new VBox(5, passwordLabel, passwordField);
        passwordBox.setAlignment(Pos.CENTER_LEFT);


        Label message = new Label();
        message.setStyle("-fx-text-fill: red;");


        Button loginBtn = new Button("Login");
        loginBtn.setPrefHeight(40);
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.getStyleClass().add("submit-button");

        loginBtn.setOnAction(e -> handleLogin(usernameField, passwordField, message));


        form.getChildren().addAll(
                title,
                usernameBox,
                passwordBox,
                message,
                loginBtn
        );
        vBox.getChildren().add(form);
        // ================= Scene =================
        root.getChildren().addAll(bgView, vBox);
        StackPane.setAlignment(form, Pos.CENTER);

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(
                getClass().getResource("/com/school/ui/frontend/css/loginStyle.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    //===========================================================================================
    private void handleLogin(TextField usernameField, PasswordField passwordField, Label message) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            message.setText("Please enter Email and password");
            return;
        }

        service.login(user, pass);

        if (service.getCurrentUser() != null) {
            User currentUser = service.getCurrentUser();

            switch (currentUser.getRole()) {
                case "ADMIN" -> new AdminView(stage, service).show();
                case "MAGAZINER" -> new MagazinerView(stage, service).show();
                case "EMPLOYEE" -> new EmployeeView(stage, service).show();
                default -> message.setText("Unknown user role");
            }
        } else {
            message.setText("Invalid username or password");
        }
    }
}
