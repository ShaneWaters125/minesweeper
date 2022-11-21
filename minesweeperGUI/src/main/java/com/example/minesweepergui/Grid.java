package com.example.minesweepergui;

import javafx.scene.layout.Pane;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Grid {

    private static int cols, rows;

    private Pane root; //Parent panel
    private static Tile[][] tiles; //2d array of all the tiles
    private static Scanner input = new Scanner(System.in);

    private static boolean lost = false; //Boolean which denotes if the game has been lost or not.
    private static ArrayList<Tile> checked = new ArrayList<>(); //Array of checked tiles so when we recursively reveal tiles we don't ask the same tile twice.

    private int bombTotal = 0;
    public static int flagsLeft;

    /**
     *
     * @param cols Number of columns
     * @param rows Number of rows
     * @param root The parent panel for the grid
     */
    public Grid(int cols, int rows, Pane root){
        if(cols == 0 | rows == 0){
            System.exit(0);
        }
        Grid.cols = cols;
        Grid.rows = rows;
        this.root = root;
        tiles = new Tile[cols][rows];
        generateGrid();
    }

    /**
     * Fills out the 2d array with tiles and makes 20% of them bombs.
     * Also adds all the tiles to the parent panel for javaFX to display.
     */
    public void generateGrid(){
        checked.clear();
        int rand = 0;
        for(int col = 0; col < cols; col++){
            for(int row = 0; row < rows; row++){
                rand = (int) (Math.random()*5) + 1; //There is roughly 1 bomb every 4.85 tiles, so a close to 20% chance of a bomb.
                Tile tile;
                if(rand == 1){
                    bombTotal += 1;
                    tile = new Tile(col, row, TileENUM.BOMB);
                    tiles[col][row] = tile;
                } else{
                    tile = new Tile(col, row, TileENUM.EMPTY);
                    tiles[col][row] = tile;
                }
                //Add the tile to the parent panel Since tiles are stackpanes they are just appended one after another onto the blank main panel.
                root.getChildren().add(tile);
            }
        }
        //Setting the number of flags to the total number of bombs.
        MinesweeperApplication.bombText.setText(Integer.toString(bombTotal));
        flagsLeft = bombTotal;
    }

    /**
     * Takes in a coordinate and sets the tile to cleared, this means the tile has been clicked on.
     * @param x coordinate
     * @param y coordinate
     */
    public static void clearTile(int x, int y){
        try{
            if(checkValidInput(x, y)){
                Tile tile = tiles[x][y];
                //If we press a bomb then the game is over.
                if(tile.getType() == TileENUM.BOMB){
                    System.out.println("GAME OVER!");
                    lost = true;
                    printGrid(true);
                }else{
                    //If we press on a non-bomb tile then we need to reveal all appropriate connected tiles.
                    autoClear(x, y);
                    System.out.println("");
                }
            } else{
                System.out.println("Invalid Input!");
            }
        } catch (InputMismatchException e){
            System.out.println("Invalid Input!");
            input = new Scanner(System.in);
        }

    }

    /**
     * Checks to see if a coordinate is valid.
     * @param x coordinate
     * @param y coordinate
     * @return Is the coordinate valid or not.
     */
    public static boolean checkValidInput(int x, int y){
        return x >= 0 && x < cols && y >= 0 && y < rows;
    }

    /**
     * Main method for revealing tiles, if a tile is connected to a tile which has 0 bombs then that tile must also be revealed and all the tiles around that must be revealed.
     * We recursively do this for every tile that we find which has no bombs around it.
     * @param x
     * @param y
     */
    public static void autoClear(int x, int y){
        boolean nearBomb = false;
        if(tiles[x][y].getType() != TileENUM.BOMB){
            //When a tile is pressed, we reveal it (cleared).
            tiles[x][y].setCleared(true);
            //Tiles are added to a list to make sure we don't recursively check the same tile twice.
            Tile tile = new Tile(x, y, TileENUM.EMPTY);
            checked.add(tile);

            //To make sure more tiles than intended are not revealed, we must check to see if the tile borders a bomb since we auto reveal every square around a press.
            if(calcBombs(x, y) > 0){
                nearBomb = true;
            }

            //For every tile around the current tile, reveal the tile if it's not a bomb and recursively call this method with neighbouring tiles of bomb count 0.
            for(int col = x-1; col <= x+1; col++){
                for(int row = y-1; row <= y+1; row++){
                    //Making sure we don't go off the grid
                    if(!(col < 0 || row < 0) && !(col > cols-1 || row > rows-1)){
                        Tile newTile = new Tile(col, row, TileENUM.EMPTY);

                        //Don't reveal tiles around the current tile if the current tile is next to a bomb.
                        if(!nearBomb){
                            tiles[col][row].setCleared(true);
                        }

                        //Recursively call this function if a neighbouring tile is 0, has no bombs next to it, and has not been checked before.
                        if(calcBombs(col, row) == 0 && !checked.contains(newTile)){
                            autoClear(col, row);
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the grid for the UI. If a tile has been cleared (revealed), its number of bombs around it will be displayed.
     * @param showBombs
     */
    public static void updateGrid(boolean showBombs){
        if(showBombs){
            for(int col = 0; col < cols; col++){
                for(int row = 0; row < rows; row++){
                    if(tiles[col][row].getType() == TileENUM.BOMB){
                        tiles[col][row].show("B");
                    }
                }
            }
        }else{
            for(int col = 0; col < cols; col++){
                for(int row = 0; row < rows; row++){
                    if(tiles[col][row].isCleared()){
                        tiles[col][row].show(calcBombs(col, row));
                    }
                }
            }
        }
    }

    /**
     * Whenever the user reveals a tile, we must check to see if they have met the win condition.
     * @return Whether the user has won or not.
     */
    public static boolean checkWin(){
        if(lost){
            return false;
        }
        Tile tile;
        //If all tiles except bombs are revealed then the user has won.
        for(int col = 0; col < cols; col++){
            for(int row = 0; row < rows; row++){
                tile = tiles[col][row];
                if(tile.getType() == TileENUM.EMPTY && !tile.isCleared()){
                    return true;
                }
            }
        }
        System.out.println("You WON!");
        return false;
    }

    /**
     * Calculates the number of bombs around a tile.
     * @param col the x position of the tile.
     * @param row the y position of the tile.
     * @return How many bombs there are around a tile.
     */
    public static int calcBombs(int col, int row){
        int bombTotal = 0;
        for(int x = col-1; x <= col+1; x++){
            for(int y = row-1; y <= row+1; y++){
                if(!(x < 0 || y < 0) && !(x > cols-1 || y > rows-1)){
                    if(tiles[x][y].getType() == TileENUM.BOMB){
                        bombTotal += 1;
                    }
                }
            }
        }
        return bombTotal;
    }

    /**
     * Prints the grid using nasty stuff.
     * @param showBombs Print the grid with bombs or not.
     */
    public static void printGrid(boolean showBombs){
        Tile tile;
        System.out.print("    ");
        for(int x = 0; x<rows; x++){
            if(x < 10){
                System.out.print( x + "  ");
            } else if(x < 100){
                System.out.print( x + " ");
            }
        }
        System.out.println();
        for(int col = 0; col <  cols ; col++){ //cols
            System.out.print(col);
            if(col < 10){
                System.out.print(" ");
            }
            for(int row = 0; row < rows; row++){ //rows
                System.out.print("  ");
                tile = tiles[col][row];
                if(tile.isFlagged()){
                    System.out.print("F");
                }else{
                    if(tile.isCleared()){
                        System.out.print(calcBombs(col, row));
                    } else{
                        if(tile.getType() == TileENUM.EMPTY){
                            System.out.print("-");
                        } else{
                            if(showBombs){
                                System.out.print("B");
                            } else{
                                System.out.print("-");
                            }
                        }
                    }
                }
                if(row == rows-1){
                    System.out.println();
                }
            }
        }
    }
}
