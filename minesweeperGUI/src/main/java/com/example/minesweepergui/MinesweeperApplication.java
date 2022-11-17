package com.example.minesweepergui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MinesweeperApplication extends Application {

    private static final int TILE_SIZE = 40; //Size of each tile (square).
    private static int width; //Width of game window
    private static int height; //Height of game window

    private static int X_TILES; //Number of tiles in x axis
    private static int Y_TILES; //Number of tiles in y axis

    private static Stage stage; //JavaFX Base Window.

    public static Text bombText = new Text(); //Number of flags left / bombs left to find
    public static Text timerText = new Text(); //Time passed

    private static boolean interrupted; //Determines when to stop all threads
    private static ScheduledExecutorService executor; //Used to schedule threads

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

        

        //Set game window icon
        stage.getIcons().add(new Image(MinesweeperApplication.class.getResource("Minesweeper_face_new.png").toExternalForm()));
        //Restart timer counting thread for when players replay so we never have more then 1 timer thread running.
        if(executor != null){
            executor.shutdown();
        }
        //Displays the pre game window
        gameDialog();
        //Displays the game window
        Scene scene = new Scene(createGame());
        stage.setTitle("Minesweeper");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initialises all the UI.
     * @return A parent object so JavaFX can display it as the base scene.
     */
    private static Parent createGame(){
        ////Main window
        Pane root = new Pane();
        //If the window is too small, force a min size.
        if(X_TILES < 8){
            root.setPrefSize(TILE_SIZE*8, height+64); //64 is padding so the bottom of the game window isn't cut off due to the topbar.
        }else{
            root.setPrefSize(width, height+64);
        }


        ////Grid to hold everything
        GridPane gridPane = new GridPane();
        //Adding grid to main window
        root.getChildren().add(gridPane);

        ////Top Bar
        HBox topBar = new HBox();

        //If the window is too small, force a min size.
        if(X_TILES < 8){
            topBar.setPrefSize(TILE_SIZE*8, 60);
        }else{
            topBar.setPrefSize(width, 60);
        }
        topBar.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        topBar.setAlignment(Pos.CENTER);

        //If the window is too small, force a min amount of spacing
        if(X_TILES < 8){
            topBar.setSpacing(25);
        }else{
            topBar.setSpacing((width/2)-150);
        }

        gridPane.add(topBar, 0, 0);

        ////Bomb Pane
        StackPane bombPane = new StackPane();
        Rectangle bombBackground = new Rectangle(100, 50);
        bombBackground.setFill(Color.BLACK);
        bombPane.getChildren().add(bombBackground);

        ////Bomb Text
        bombText.setText("032");
        bombText.setFill(Color.RED);
        bombText.setFont(Font.font("Geoslab703 Md Bt", FontWeight.BOLD, 48));
        bombPane.getChildren().add(bombText);

        //Adding Bomb Pane to topbar
        topBar.getChildren().add(bombPane);

        ////Cursed button
        Button button = new Button();
        button.setPrefSize(50,50);
        Image cursedface = new Image(MinesweeperApplication.class.getResource("Minesweeper_face_new.png").toExternalForm());
        button.setBackground(new Background(new BackgroundImage(cursedface, null, null, null, new BackgroundSize(50, 50, false, false, false, false))));
        topBar.getChildren().add(button);

        button.setOnMouseClicked(e -> {
            startGame();
        });

        ////Timer Pane
        StackPane timePane = new StackPane();
        Rectangle timeBackground = new Rectangle(100, 50);
        timeBackground.setFill(Color.BLACK);
        timePane.getChildren().add(timeBackground);

        ////timer Text
        timerText.setText("000");
        timerText.setFill(Color.RED);
        timerText.setFont(Font.font("Geoslab703 Md Bt", FontWeight.BOLD, 48));
        timePane.getChildren().add(timerText);

        startTimer();

        //Adding timer pane to topbar
        topBar.getChildren().add(timePane);



        ////Pane for the game to go in
        Pane newRoot = new Pane();
        gridPane.add(newRoot, 0, 1);

        //Create game grid of entered size.
        Grid grid = new Grid(X_TILES, Y_TILES, newRoot);
        return root;
    }

    /**
     * Starts a thread which keeps track of the time to display ingame.
     */
    private static void startTimer(){
        executor = Executors.newScheduledThreadPool(1);
        Runnable countTime = new Runnable() {
            @Override
            public void run() {
                //Making sure we don't get a memory leak when closing the game.
                if(interrupted){
                    executor.shutdown();
                }
                int curTime = Integer.parseInt(timerText.getText());
                //Stop counting at 999 just like classic minesweeper
                if(curTime < 999){
                    timerText.setText(Integer.toString(curTime+1));
                }
            }
        };

        executor.scheduleAtFixedRate(countTime, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Prompts the user to input the size they would like the grid before the game is started.
     */
    public static void gameDialog(){
        //Creating the dialog window.
        Dialog<Object> dialog = new Dialog<>();
        dialog.setTitle("Minesweeper Setup");
        dialog.setHeaderText("Enter your game size (1-29)");

        //Setting dialog window to use same icon as the game.
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(MinesweeperApplication.class.getResource("Minesweeper_face_new.png").toExternalForm()));

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
                //Calculating game window size.
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

    @Override
    public void stop(){
        interrupted = true;
    }
}