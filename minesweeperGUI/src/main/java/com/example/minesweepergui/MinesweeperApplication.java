package com.example.minesweepergui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class MinesweeperApplication extends Application {

    private static final int TILE_SIZE = 40;
    private static int width;
    private static int height;

    private static int X_TILES; //Number of tiles in x axis
    private static int Y_TILES; //Number of tiles in y axis

    private static Stage stage;


    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
        stage.setResizable(false);
        MinesweeperApplication.stage = stage;
        startGame();
    }

    /**
     * Starts the game
     */
    public static void startGame(){
        //Displays the pre game window
        gameDialog();
        //Displays the game window
        Scene scene = new Scene(createGame());
        stage.setScene(scene);
        stage.show();
    }
    private static Parent createGame(){
        Pane root = new Pane();
        root.setPrefSize(width, height);

        //Create game grid of entered size.
        Grid grid = new Grid(X_TILES, Y_TILES, root);
        return root;
    }

    /**
     * Prompts the user to input the size they would like the grid before the game is started.
     */
    public static void gameDialog(){
        //Creating the dialog window.
        Dialog<Object> dialog = new Dialog<>();
        dialog.setTitle("Minesweeper Setup");
        dialog.setHeaderText("Enter your game size (1-29)");

        //Creating the buttons for the dialog window.
        ButtonType startGameType = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(startGameType, ButtonType.CLOSE);

        //Creating a grid to layout the texts and input boxes.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField columns = new TextField();
        columns.setPromptText("Columns");
        TextField rows = new TextField();
        rows.setPromptText("Rows");

        //Adding JavaFX objects to the grid.
        grid.add(new Label("No. Columns:"), 0, 0);
        grid.add(columns, 1, 0);
        grid.add(new Label("No. Rows"), 0, 1);
        grid.add(rows, 1, 1);

        //Set the focus of the dialog to the first input box (columns)
        dialog.getDialogPane().setContent(grid);
        Platform.runLater(columns::requestFocus);



        Node startButton = dialog.getDialogPane().lookupButton(startGameType);
        startButton.setDisable(true);

        AtomicBoolean colValid = new AtomicBoolean(false);
        AtomicBoolean rowValid = new AtomicBoolean(false);

        columns.textProperty().addListener((colObservable, colOldValue, colNewValue) -> {
//            colValid.set(colNewValue.matches("[1-9][0-9]*"));
            colValid.set(colNewValue.matches("[1-2]?[0-9]"));
            if(colValid.get() && rowValid.get()){
                startButton.setDisable(false);
            }else{
                startButton.setDisable(true);
            }
        });

        rows.textProperty().addListener((rowObservable, rowOldValue, rowNewValue) -> {
            rowValid.set(rowNewValue.matches("[1-2]?[0-9]"));
            if(colValid.get() && rowValid.get()){
                startButton.setDisable(false);
            }else{
                startButton.setDisable(true);
            }
        });



        //Tells dialog to show and wait until a result is given which is when the user presses the start button.
        Optional<Object> result = dialog.showAndWait();
        result.ifPresent(columnsRows -> {
            try{
                int w = Integer.parseInt(columns.getText());
                int h = Integer.parseInt(rows.getText());
                if((w > 0 && h > 0 && w < 60 && h < 40)){
                    width = Integer.parseInt(columns.getText()) * TILE_SIZE;
                    height = Integer.parseInt(rows.getText()) * TILE_SIZE;
                    X_TILES = (width /TILE_SIZE);
                    Y_TILES = (height /TILE_SIZE);
                }else{
                    gameDialog();
                }
            } catch(Exception e){
                System.out.println("pain");
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}