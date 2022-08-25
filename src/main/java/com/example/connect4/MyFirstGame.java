package com.example.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class MyFirstGame extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MyFirstGame.class.getResource("game.fxml"));
        GridPane rootGridPane = loader.load();

        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();          // Calling Method of createMenu from within our start method
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());  // whatever is the width of primaryStage will now be the width of our menuBar

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0); // getting the reference to this Pane within our Main.java
        menuPane.getChildren().add(menuBar);   // Adding the menuBar as a child element into Pane

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    private MenuBar createMenu() {

        // File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");                  // Created MenuItem for fileMenu
        // Handling the click event on New Button : Lambda Expression
        newGame.setOnAction(actionEvent -> {       // Using Lambda in place of EventHandler to make it look good
            controller.resetGame();
        });

        MenuItem resetGame = new MenuItem("Reset Game");                 // Created MenuItem for fileMenu
        // Handling the click event on Reset Button
        resetGame.setOnAction(actionEvent -> controller.resetGame());     // Using Lambda in place of EventHandler to make it look good


        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();   // Separates MenuItems of fileMenu by drawing Line

        MenuItem exitGame = new MenuItem("Exit Game");
        // Handling the click event on Exit Button
        exitGame.setOnAction(actionEvent -> {        // Using Lambda in place of EventHandler to make it look good
            exitGame();
        });

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);    // Adding MenuItems into fileMenu (to get drop down list)

        // Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About Connect4");     // Created MenuItem for helpMenu
        // Handling the click event on About Button
        aboutGame.setOnAction(actionEvent -> aboutConnect4());

        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();

        MenuItem aboutMe = new MenuItem("About Me");     // Created MenuItem for helpMenu
        // Handling the click event on About Button
        aboutMe.setOnAction(actionEvent -> aboutMe());

        helpMenu.getItems().addAll(aboutGame, separatorMenuItem1, aboutMe);            // Adding MenuItems into helpMenu (to get drop down list)

        // Menu Bar : The whole Menu Pane at the top of Application
        MenuBar menuBar = new MenuBar();                // Creating MenuBar to include all Menus
        menuBar.getMenus().addAll(fileMenu, helpMenu);   // Adding Menus inside MenuBar

        return menuBar;
    }

    private void exitGame() {
        Platform.exit();                           // Shut down the current application
        System.exit(0);                      // Shut down the current Virtual Machine
    }

    private void resetGame() {


    }

    private void aboutMe() {

        // Alert Dialog
        Alert alertDialog = new Alert(Alert.AlertType.INFORMATION);
        alertDialog.setTitle("About the Developer");
        alertDialog.setHeaderText("Rahul Singh");
        alertDialog.setContentText("Connect Four is a two-player connection game in which " +
                "the players first choose a color and then take turns dropping colored discs " +
                "from the top into a seven-column, six-row vertically suspended grid.");
        alertDialog.show();
    }

    private void aboutConnect4() {

        // Alert Dialog
        Alert alertDialog = new Alert(Alert.AlertType.INFORMATION);
        alertDialog.setTitle("About Connect Four");
        alertDialog.setHeaderText("How to Play ?");
        alertDialog.setContentText("Connect Four is a two-player connection game in which " +
                "the players first choose a color and then take turns dropping colored discs " +
                "from the top into a seven-column, six-row vertically suspended grid. " +
                "The pieces fall straight down, occupying the next available space within the column. " +
                "The objective of the game is to be the first to form a horizontal, vertical, " +
                "or diagonal line of four of one's own discs. Connect Four is a solved game. " +
                "The first player can always win by playing the right moves");
        alertDialog.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}