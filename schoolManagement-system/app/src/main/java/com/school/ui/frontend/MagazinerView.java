
package com.school.ui.frontend;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MagazinerView {

        private Stage stage;

        public MagazinerView(Stage stage) {
                this.stage = stage;
        }
        public void show() {

        BorderPane root = new BorderPane();
        Image bgImage = new Image(
            "file:/home/someone/Desktop/project1/project1/app/src/main/java/com/school/ui/frontend/img/magazine-bg.png"
        );
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(1200);
        bgView.setFitHeight(700);
        root.getChildren().add(bgView);

        /* ================= HEADER ================= */
        HBox header = new HBox(20);
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(10, 20, 10, 20));

        
        BorderPane.setMargin(header, new Insets(20, 40, 0, 40));

        // Notification icon
        ImageView notifIcon = new ImageView(
                new Image("file:/home/someone/Desktop/project1/project1/app/src/main/java/com/school/ui/frontend/img/notification.png")
        );
        notifIcon.setFitWidth(20);
        notifIcon.setFitHeight(20);

        Button notificationBtn = new Button();
        notificationBtn.setGraphic(notifIcon);
        notificationBtn.getStyleClass().add("icon-btn");

        // Date & Time
        Label dateTime = new Label();
        dateTime.getStyleClass().add("date-time");

        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                        DateTimeFormatter formatter =
                                DateTimeFormatter.ofPattern("EEEE dd/MM/yyyy  HH:mm:ss");
                        dateTime.setText(LocalDateTime.now().format(formatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        // User
        ImageView userImg = new ImageView(new Image("file:/home/someone/Desktop/project1/project1/app/src/main/java/com/school/ui/frontend/img/user.png"));
        userImg.setFitWidth(32);
        userImg.setFitHeight(32);

        Label userName = new Label("Someone");
        userName.getStyleClass().add("user-name");

        HBox userBox = new HBox(10, userImg, userName);
        userBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        header.getChildren().addAll(notificationBtn, spacer1, dateTime, spacer2, userBox);

        /* ================= TITLE ================= */
        Label title = new Label("Magazine");
        title.getStyleClass().add("page-title");

        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);

        /* ================= SEARCH + FILTER (LEFT) ================= */
        MenuButton filterMenu = new MenuButton("Filter");
        MenuItem allItem = new MenuItem("ID");
        MenuItem nameItem = new MenuItem("Name");
        MenuItem categoryItem = new MenuItem("Category"); 
        filterMenu.getItems().addAll(allItem, nameItem, categoryItem);

        filterMenu.getStyleClass().add("filter-menu");

        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.getStyleClass().add("search-field");

        HBox searchBar = new HBox(10, filterMenu, searchField);
        searchBar.setAlignment(Pos.CENTER_RIGHT);

        /* ================= ACTION BUTTONS (RIGHT) ================= */
        Button reserveBtn = new Button("Reserve");
        reserveBtn.getStyleClass().add("reserve-btn");

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("delete-btn");

        HBox actionBtns = new HBox(10, reserveBtn, deleteBtn);
        actionBtns.setAlignment(Pos.CENTER_LEFT);

        BorderPane toolsBar = new BorderPane();
        toolsBar.setLeft(actionBtns);
        toolsBar.setRight(searchBar);

        /* ================= TABLE ================= */
        TableView<Object> table = new TableView<>();
        table.getStyleClass().add("magazine-table");

        TableColumn<Object, Boolean> selectCol = new TableColumn<>("✔");
        selectCol.setPrefWidth(40);

        TableColumn<Object, Integer> idCol = new TableColumn<>("ID");
        TableColumn<Object, String> nameCol = new TableColumn<>("Name");
        TableColumn<Object, Integer> totalCol = new TableColumn<>("Total");
        TableColumn<Object, Integer> remainCol = new TableColumn<>("Remaining");
        TableColumn<Object, String> categoryCol = new TableColumn<>("Category");
        TableColumn<Object, String> statusCol = new TableColumn<>("Status");

        table.getColumns().addAll(
                selectCol, idCol, nameCol, totalCol, remainCol, categoryCol, statusCol
        );


        Button addBtn = new Button();
        ImageView addIcon = new ImageView(new Image("file:/home/someone/Desktop/project1/project1/app/src/main/java/com/school/ui/frontend/img/add-element.png"));
        addIcon.setFitWidth(20);
        addIcon.setFitHeight(20);
        addBtn.setGraphic(addIcon);
        addBtn.getStyleClass().add("icon-btn");
        addBtn.setOnAction(e -> {
                AddElementDialog addDialog = new AddElementDialog(stage);
                addDialog.showAndWait();
        });


        Button viewBtn = new Button();
        ImageView viewIcon = new ImageView(new Image("file:/home/someone/Desktop/project1/project1/app/src/main/java/com/school/ui/frontend/img/element-info.png"));
        viewIcon.setFitWidth(20);
        viewIcon.setFitHeight(20);
        viewBtn.setGraphic(viewIcon);
        viewBtn.getStyleClass().add("icon-btn");
        viewBtn.setOnAction(e -> {
                ViewElementDialog viewDialog = new ViewElementDialog(stage, 1); // Example element ID
                viewDialog.showAndWait();
        });


        Button editBtn = new Button();
        ImageView editIcon = new ImageView(new Image("file:/home/someone/Desktop/project1/project1/app/src/main/java/com/school/ui/frontend/img/edit-info.png"));
        editIcon.setFitWidth(20);
        editIcon.setFitHeight(20);
        editBtn.setGraphic(editIcon);
        editBtn.getStyleClass().add("icon-btn");

        editBtn.setOnAction(e -> {
                EditElementDialog editDialog = new EditElementDialog(stage, 1); // Example element ID
                editDialog.showAndWait();
        });

        /* ================= SIDE ICONS (LEFT) ================= */
        VBox sideActions = new VBox(
                15,
                addBtn,
                viewBtn,
                editBtn        
        );
        sideActions.getStyleClass().add("side-actions");
        sideActions.setAlignment(Pos.TOP_CENTER);

        HBox tableSection = new HBox(15, sideActions, table);
        HBox.setHgrow(table, Priority.ALWAYS);

        VBox center = new VBox(20, titleBox, toolsBar, tableSection);
        center.setPadding(new Insets(20, 20, 40, 20));

        root.setTop(header);
        root.setCenter(center);

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(
                getClass().getResource("/com/school/ui/frontend/css/magazine.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.show();
        }

        
    
}