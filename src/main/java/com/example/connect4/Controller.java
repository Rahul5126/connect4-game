package com.example.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

               // GAME RULES
    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final int CIRCLE_DIAMETER = 80;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";

    private static String PLAYER_ONE = "Player One";
    private static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOneTurn = true;

    private Disc[][] insertedDiscArray = new  Disc [ROWS] [COLUMNS];    // For Structural Changes : For the Developers

    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane insertedDiscsPane;

    @FXML
    public Label playerNameLabel;

    private boolean isAllowedToInsert = true;     // Flag to avoid same color disc being added

    public void createPlayground() {

        Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);

        // Loop for getting Circle in each of the Rows And Columns in our Rectangle Sheet
        for (int row = 0; row < ROWS ; row++) {

            for (int col = 0; col < COLUMNS; col++) {

                // Creating Circles in our Rectangle Sheet
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2);
                circle.setCenterY(CIRCLE_DIAMETER / 2);
                circle.setSmooth(true);

// with each iteration we are subtracting our circle from the subsequent position (for each Row & Column)
                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
            }
        }

        rectangleWithHoles.setFill(Color.WHITE);

        rootGridPane.add(rectangleWithHoles, 0 , 1);  // Adding rectangle inside Pane 2

        List<Rectangle> rectangleList = createClickableRectangle();

        for (Rectangle rectangle : rectangleList) {
            rootGridPane.add(rectangle,0, 1);
        }


    }

                      // Adding Rectangle
    private List<Rectangle> createClickableRectangle() {

        List<Rectangle> rectangleList = new ArrayList<>();

        for (int col = 0; col < COLUMNS; col++) {

            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

               // Rectangle With Hover Effect
            rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));

 // Disc Class : On click of each of the columns we need to insert disc into the respective columns
            final int column = col;
            rectangle.setOnMouseClicked(mouseEvent -> {
                if (isAllowedToInsert) {
                    isAllowedToInsert = false;   // When disc is being dropped then no more disc will be inserted
                    insertDisc(new Disc(isPlayerOneTurn), column);
                }
            });

            rectangleList.add(rectangle);
        }


        return rectangleList;

    }

    private void insertDisc(Disc disc, int column) {

                  // Placing Discs on top of each other
        int row = ROWS - 1;
        while (row >= 0) {
            if (getDiscIfPresent(row, column) == null)
                break;
            row--;
        }
        if (row < 0)             // if it is full, we cannot insert anymore disc
            return;

        insertedDiscArray [row] [column] = disc;       // For Structural Changes : For Developers
        insertedDiscsPane.getChildren().add(disc);
        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);    // for getting disc at specific position
                          // Translate Animation
        int currentRow = row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5) , disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

                            // Toggling between Players
        translateTransition.setOnFinished(actionEvent ->  {

            isAllowedToInsert = true;          // Finally when disc is dropped allow next player to insert disc

            if (gameEnded (currentRow, column)) {
                gameOver();
                return;
            }

            isPlayerOneTurn = !isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
        });

        translateTransition.play();
    }

    public void gameOver() {

        String winner = isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO;
        System.out.println("Winner is: " + winner);

           // Officially Declare the Winner

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The Winner is : " + winner);
        alert.setContentText("Want to play again? ");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No, Exit");

        alert.getButtonTypes().setAll(yesBtn, noBtn);     // adding buttons to alert

        Platform.runLater(() -> {                // Helps us to resolve IllegalStateException

                            // getting the functionality of these two buttons
            Optional<ButtonType> btnClicked = alert.showAndWait();
            if (btnClicked.isPresent() && btnClicked.get() == yesBtn) {
                // ... user chose Yes so RESET the Game
                resetGame();
            } else {
                // ... user chose No so EXIT the Game
                Platform.exit();
                System.exit(0);
            }

        });
    }

    public void resetGame() {

        insertedDiscsPane.getChildren().clear();    // Remove all inserted Disc from Pane : For Visual Change

        for (Disc[] discs : insertedDiscArray) {    // Structurally, make all elements of insertedDiscArray[][] to null

            Arrays.fill(discs, null);
        }

        isPlayerOneTurn = true;       // Let Player 1 start the game
        playerNameLabel.setText(PLAYER_ONE);

        createPlayground();         // Prepare a fresh playground
    }

    private boolean gameEnded(int row, int column) {

                     // Wining Criteria
        //Vertical points. A small example : Player has inserted his last disc at row=2 and column=3
        //range of values = 0,1,2,3,4,5
        //index of each element present in column [row][column] = [0,3] [1,3] [2,3] [3,3] [4,3] [5,3] --> Point2D (Java Class): holds the value in terms of X & Y coordinates
        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)    // comment 2
                .mapToObj(r -> new Point2D(r, column))                           // comment 3
                .collect(Collectors.toList());

        List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
                .mapToObj(col -> new Point2D(row, col))
                .collect(Collectors.toList());

                           // Diagonal Check
        Point2D startPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint1.add(i, -i))
                .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint2.add(i, i))
                .collect(Collectors.toList());

        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                            || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

        return isEnded;
    }

    private boolean checkCombinations(List<Point2D> points) {

        int chain = 0;

        for (Point2D point : points) {

            int rowIndexForArray = (int) point.getX();
            int colIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, colIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {     // if the last inserted disc belongs to the current player

                chain ++;
                if (chain == 4) {
                    return true;
                }

            }  else {
                chain = 0;
            }
        }
        return  false;
    }

    private Disc getDiscIfPresent(int row, int column) {          // to prevent ArrayIndexOutOfBoundException

        if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0)   // If row and column index is invalid
            return null;

        return insertedDiscArray [row][column];

    }

    private static class Disc extends Circle{
        private final boolean isPlayerOneMove;
        public Disc (boolean isPlayerOneMove) {
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER / 2);
            setFill(isPlayerOneMove? Color.valueOf(discColor1) : Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER / 2);
            setCenterY(CIRCLE_DIAMETER / 2);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}