package com.example.minesweepergui;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.Optional;

public class Tile extends StackPane {

    private static final int TILE_SIZE = 40; //Size of tile in pixels (square).
    private int x, y;
    private TileENUM type; //If tile is empty or bomb.
    private boolean isFlagged, isCleared;

    private Rectangle border = new Rectangle(TILE_SIZE, TILE_SIZE); //border is the UI representation of the tile.
    private Text value = new Text();

    /**
     * Represents a single tile on the grid.
     * @param x The x coordinate
     * @param y The y coordinate
     * @param type Is the tile a bomb or empty.
     */
    public Tile(int x, int y, TileENUM type){
        this.x = x;
        this.y = y;
        this.type = type;
        //Setting the font of the tile
        value.setFont(Font.font("Geoslab703 Md Bt", FontWeight.BOLD, 28));
        border.setStroke(Color.LIGHTGRAY);
        //Setting the image of every tile to a default minesweeper square.
        Image image = new Image(MinesweeperApplication.class.getResource("Minesweeper_unopened_square.png").toExternalForm());
        border.setFill(new ImagePattern(image));

        value.setVisible(false);

        //Assigns the border and value to the tile otherwise each tile has no UI representation.
        getChildren().addAll(border, value);

        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);

        //Left click = clear a tile
        //Right click = flag a tile
        setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                //Can't reveal / clear a tile which is flagged and can't click on tiles that are already revealed.
                if(!value.getText().equals("F") && !isCleared){
                    open();
                    //Checking if the user wins when clearing a tile.
                    if(!Grid.checkWin()){
                        gameWon();
                    }
                    System.out.println("clear");
                }
            } else if(e.getButton() == MouseButton.SECONDARY){
                //Flagging and unflagging.
                if(value.getText().equals("F")){
                    Grid.flagsLeft += 1;
                    MinesweeperApplication.bombText.setText(Integer.toString(Grid.flagsLeft));
                    System.out.println("unflag");
                    unshow();
                }else{
                    System.out.println("flag");
                    //Cant flag a tile which is already revealed / cleared.
                    if(!isCleared && Grid.flagsLeft != 0){
                        Grid.flagsLeft -= 1;
                        MinesweeperApplication.bombText.setText(Integer.toString(Grid.flagsLeft));
                        show("F");
                    }
                }
            }
        });
    }

    /**
     * When a tile is pressed, we either start the recursively clearing of tiles or lose because it was a bomb, the grid is then updated.
     */
    public void open(){
        if(this.type == TileENUM.BOMB){
            Grid.updateGrid(true);
            gameOver();
        } else{
            Grid.clearTile(x, y);
            Grid.updateGrid(false);
            Grid.printGrid(false);
        }
    }

    /**
     * Opens up the game over dialog popup box.
     */
    public void gameOver(){
        Dialog<Object> gameOverDialog = new Dialog<>();
        gameOverDialog.setTitle("Minesweeper");
        gameOverDialog.setHeaderText("You LOST!");

        Image image = new Image(MinesweeperApplication.class.getResource("Minesweeper_stoned.png").toExternalForm());
        Pane pane = new Pane();
        HBox hBox = new HBox();
        hBox.setPrefSize(400, 400);
        hBox.setBackground(new Background(new BackgroundImage(image, null, null, null, new BackgroundSize(50, 50, true, true, true, true))));
        pane.getChildren().add(hBox);

        gameOverDialog.getDialogPane().setContent(pane);

        gameOverDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        //Tells dialog to show and wait until a result is given which is when the user presses the close button.
        Optional<Object> result = gameOverDialog.showAndWait();
        result.ifPresent(retry -> {
            MinesweeperApplication.startGame();
        });
    }

    /**
     * Opens up the game won dialog popup box.
     */
    public void gameWon(){
        Dialog<Object> gameOverDialog = new Dialog<>();
        gameOverDialog.setTitle("Minesweeper");
        gameOverDialog.setContentText("You WON!");

        gameOverDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        //Tells dialog to show and wait until a result is given which is when the user presses the close button.
        Optional<Object> result = gameOverDialog.showAndWait();
        result.ifPresent(retry -> {
            System.exit(0);
        });
    }

    /**
     * If the cleared tile has a number, next to n bombs, display that number.
     * @param number the number to be assigned to the tile.
     */
    public void show(int number){
        if(number != 0){
            value.setText(Integer.toString(number));
            switch (number) {
                case 1 -> value.setFill(Color.BLUE);
                case 2 -> value.setFill(Color.GREEN);
                case 3 -> value.setFill(Color.RED);
                case 4 -> value.setFill(Color.PURPLE);
                case 5 -> value.setFill(Color.MAROON);
                case 6 -> value.setFill(Color.TURQUOISE);
                case 7 -> value.setFill(Color.BLACK);
                case 8 -> value.setFill(Color.GRAY);
            }
            value.setVisible(true);
        }
        border.setFill(null);
    }

    /**
     * If the tile is shown and has no number to display, it's either a flag or bomb.
     * @param character the character to be assigned to the tile.
     */
    public void show(String character){
        value.setText(character);
        border.setFill(null);
        if(character.equals("F")){
            Image image = new Image(MinesweeperApplication.class.getResource("Minesweeper_flag.png").toExternalForm());
            border.setFill(new ImagePattern(image));
        } else if(character.equals("B")){
            Image image = new Image(MinesweeperApplication.class.getResource("Minesweeper_bomb.png").toExternalForm());
            border.setFill(new ImagePattern(image));
        }
    }

    /**
     * Rehides a tile, used when unflagging.
     */
    public void unshow(){
        value.setText(null);
        Image image = new Image(MinesweeperApplication.class.getResource("Minesweeper_unopened_square.png").toExternalForm());
        border.setFill(new ImagePattern(image));
    }

    public boolean isCleared() {
        return isCleared;
    }

    public void setCleared(boolean cleared) {
        isCleared = cleared;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public TileENUM getType() {
        return type;
    }

    public void setType(TileENUM type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return x == tile.x && y == tile.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                ", type=" + type +
                '}';
    }
}
