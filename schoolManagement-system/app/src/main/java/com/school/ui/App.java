package com.school.ui;


import com.school.backend.service.SchoolService;
import javafx.application.Application;
import javafx.stage.Stage;
import com.school.ui.frontend.LoginView;


public class App extends Application {

    @Override
    public void start(Stage stage) {
        SchoolService service = SchoolService.getInstance();
        LoginView login = new LoginView(stage);
        login.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
