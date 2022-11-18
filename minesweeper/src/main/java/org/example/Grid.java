package org.example;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Grid {

    private int cols, rows; //Number of cols and rows.
    private final Tile[][] tiles; //2D Array holding all the tiles.
    private Scanner input = new Scanner(System.in); //System input.

    private boolean lost = false; //When true, game will end.
    private ArrayList<Tile> checked = new ArrayList<>(); //An array holding all the tiles which have been cleared so they are not cleared again.

    public Grid(int cols, int rows){
        this.cols = cols;
        this.rows = rows;
        tiles = new Tile[cols][rows];
        generateGrid();
        while(checkWin()){
            playGame();
        }
    }

    /**
     * Fills out the 2d array with tiles and makes 20% of them bombs.
     * Also adds all the tiles to the parent panel for javaFX to display.
     */
    private void generateGrid(){
        int rand = 0;
        for(int col = 0; col < cols; col++){
            for(int row = 0; row < rows; row++){
                rand = (int) (Math.random()*5) + 1; //There is roughly 1 bomb every 4.85 tiles, so a close to 20% chance of a bomb.
                if(rand == 1){
                    tiles[col][row] = new Tile(col, row, TileENUM.BOMB);
                } else{
                    tiles[col][row] = new Tile(col, row, TileENUM.EMPTY);
                }
            }
        }
    }

    /**
     * Prompts the user for their input.
     */
    private void playGame(){
        input = new Scanner(System.in);
        printGrid(false);
        System.out.println("Type C: Clear Tile, F: Flag Tile, U: Unflag Tile");
        String decision = input.nextLine();
        switch (decision.toUpperCase()) {
            case "F" -> flagTile();
            case "C" -> clearTile();
            case "U" -> removeFlag();
            default -> System.out.println("");
        }
    }

    /**
     * Removes a flag.
     */
    private void removeFlag(){
        try{
            System.out.println("Enter the tiles x coordinate you would like to unflag: ");
            int y = input.nextInt();
            System.out.println("Enter the tiles y coordinate you would like to unflag: ");
            int x = input.nextInt();

            //Valid coords must be input.
            if(checkValidInput(x, y)){
                tiles[x][y].setFlagged(false);
            }else{
                System.out.println("Invalid Input!");
                removeFlag();
            }
        } catch (InputMismatchException e){
            System.out.println("Invalid Input!");
            input = new Scanner(System.in);
        }
    }

    /**
     * Flags a tile.
     */
    private void flagTile(){
        try{
            System.out.println("Enter the tiles x coordinate you would like to flag: ");
            int y = input.nextInt();
            System.out.println("Enter the tiles y coordinate you would like to flag: ");
            int x = input.nextInt();
            //Valid coords must be input.
            if(checkValidInput(x, y)){
                //Can't flag a tile which has been cleared.
                if(!tiles[x][y].isCleared()){
                    tiles[x][y].setFlagged(true);
                } else{
                    System.out.println("Can't flag a tile that is already revealed!");
                }
            }else{
                System.out.println("Invalid Input!");
                flagTile();
            }
        } catch (InputMismatchException e){
            System.out.println("Invalid Input!");
            input = new Scanner(System.in);
        }

    }

    /**
     * Asks the user what tile they want to clear.
     */
    private void clearTile(){
        try{
            System.out.println("Enter the tiles x coordinate you would like to clear: ");
            int y = input.nextInt();
            System.out.println("Enter the tiles y coordinate you would like to clear: ");
            int x = input.nextInt();

            //Valid coords must be input.
            if(checkValidInput(x, y)){
                Tile tile = tiles[x][y];
                //Can't clear a tile that has been flagged.
                if(!tile.isFlagged()){
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
                }else{
                    System.out.println("Unflag this tile if you want to flag it!");
                }
            } else{
                System.out.println("Invalid Input!");
                clearTile();
            }
        } catch (InputMismatchException e){
            System.out.println("Invalid Input!");
            input = new Scanner(System.in);
        }

    }

    private boolean checkValidInput(int x, int y){
        return x >= 0 && x < cols && y >= 0 && y < rows;
    }

    /**
     * Main method for revealing tiles, if a tile is connected to a tile which has 0 bombs then that tile must also be revealed and all the tiles around that must be revealed.
     * We recursively do this for every tile that we find which has no bombs around it.
     * @param x
     * @param y
     */
    private void autoClear(int x, int y){
        boolean nearBomb = false;
        if(tiles[x][y].getType() != TileENUM.BOMB){
            tiles[x][y].setCleared(true);
            Tile tile = new Tile(x, y, TileENUM.EMPTY);
            checked.add(tile);

            //To make sure more tiles than intended are not revealed, we must check to see if the tile borders a bomb since we auto reveal every square around a press.
            if(calcBombs(x, y) > 0){
                nearBomb = true;
            }

            //For every tile around the current tile, reveal the tile if it's not a bomb and recursively call this method with neighbouring tiles of bomb count 0.
            for(int col = x-1; col <= x+1; col++){
                for(int row = y-1; row <= y+1; row++){
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

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getHeight() {
        return rows;
    }

    public void setHeight(int height) {
        this.rows = height;
    }

    /**
     * Whenever the user reveals a tile, we must check to see if they have met the win condition.
     * @return Whether the user has won or not.
     */
    private boolean checkWin(){
        if(lost){
            return false;
        }
        Tile tile;
        //If all tiles except bombs are revealed then the user has won.
        for(int col = 0; col < cols; col++){
            for(int row = 0; row < rows; row++){
                tile = tiles[row][col];
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
    private int calcBombs(int col, int row){
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
    public void printGrid(boolean showBombs){
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
