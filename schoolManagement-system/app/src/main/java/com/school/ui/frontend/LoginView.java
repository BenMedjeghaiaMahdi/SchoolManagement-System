package com.school.ui.frontend;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginView  {
    
    private Stage stage ;

    public LoginView(Stage stage){
        this.stage = stage;
    }

    

    public void show() {

        Pane root = new Pane();

        this.stage.setTitle("School Management System");
        

        Image bgImage = new Image(
            "file:/home/someone/Desktop/project1/project1/app/src/main/java/com/school/ui/frontend/img/background.png"
        );
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(1200);
        bgView.setFitHeight(700);

        

        Label title = new Label("Login");
        title.setLayoutX(420);
        title.setLayoutY(150);
        title.getStyleClass().add("login-title");

        

        Label userNameLabel = new Label("User Name :");
        userNameLabel.setLayoutX(360);
        userNameLabel.setLayoutY(245);
        userNameLabel.getStyleClass().add("form-label");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefSize(300, 30);
        usernameField.setLayoutX(530);
        usernameField.setLayoutY(250);
        usernameField.getStyleClass().add("form-input");

        

        Label passwordLabel = new Label("Password :");
        passwordLabel.setLayoutX(360);
        passwordLabel.setLayoutY(295);
        passwordLabel.getStyleClass().add("form-label");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefSize(300, 30);
        passwordField.setLayoutX(530);
        passwordField.setLayoutY(300);
        passwordField.getStyleClass().add("form-input");

        

        Label message = new Label();
        message.setLayoutX(530);
        message.setLayoutY(345);
        message.setStyle("-fx-text-fill: red;");

        

        Button loginBtn = new Button("Login");
        loginBtn.setPrefSize(120, 40);
        loginBtn.setLayoutX(570);
        loginBtn.setLayoutY(390);
        loginBtn.getStyleClass().add("submit-button");

        

        loginBtn.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();

            if (user.equals("admin") && pass.equals("admin123")) {
                new AdminView(stage).show();
            }
            else if (user.equals("store") && pass.equals("store123")) {
                new MagazinerView(stage).show();
            }
            else if (user.equals("emp") && pass.equals("emp123")) {
                new EmployeView(stage).show();
            }
            else {
                message.setText("Username or Password incorrect!");
            }
        });

        
        root.getChildren().addAll(
            bgView,
            title,
            userNameLabel,
            usernameField,
            passwordLabel,
            passwordField,
            loginBtn,
            message
        );

        
        Scene scene = new Scene(root, 1200, 700);

        scene.getStylesheets().add(
            "file:/home/someone/Desktop/project1/project1/app/src/main/java/com/school/ui/frontend/css/loginStyle.css"
        );

        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

}

