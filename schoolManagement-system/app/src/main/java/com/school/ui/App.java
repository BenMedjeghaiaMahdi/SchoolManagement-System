package com.school.ui;


import javafx.application.Application;
import javafx.stage.Stage;
import com.school.ui.frontend.LoginView;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        LoginView login = new LoginView(stage);
        login.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
