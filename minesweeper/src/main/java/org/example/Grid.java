package org.example;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Grid {

    private int cols, rows;
    private final Tile[][] tiles;
    private Scanner input = new Scanner(System.in);

    private boolean lost = false;
    private ArrayList<Tile> checked = new ArrayList<>();

    public Grid(int cols, int rows){
        this.cols = cols;
        this.rows = rows;
        tiles = new Tile[cols][rows];
        generateGrid();
        while(checkWin()){
            playGame();
        }
    }

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

    private void removeFlag(){
        try{
            System.out.println("Enter the tiles x coordinate you would like to unflag: ");
            int y = input.nextInt();
            System.out.println("Enter the tiles y coordinate you would like to unflag: ");
            int x = input.nextInt();

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

    private void flagTile(){
        try{
            System.out.println("Enter the tiles x coordinate you would like to flag: ");
            int y = input.nextInt();
            System.out.println("Enter the tiles y coordinate you would like to flag: ");
            int x = input.nextInt();

            if(checkValidInput(x, y)){
                tiles[x][y].setFlagged(true);
            }else{
                System.out.println("Invalid Input!");
                flagTile();
            }
        } catch (InputMismatchException e){
            System.out.println("Invalid Input!");
            input = new Scanner(System.in);
        }

    }

    private void clearTile(){
        try{
            System.out.println("Enter the tiles x coordinate you would like to clear: ");
            int y = input.nextInt();
            System.out.println("Enter the tiles y coordinate you would like to clear: ");
            int x = input.nextInt();

            if(checkValidInput(x, y)){
                Tile tile = tiles[x][y];

                if(tile.getType() == TileENUM.BOMB){
                    System.out.println("GAME OVER!");
                    lost = true;
                    printGrid(true);
                }else{
                    autoClear(x, y);
                    System.out.println("");
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

    private void autoClear(int x, int y){
        boolean nearBomb = false;
        if(tiles[x][y].getType() != TileENUM.BOMB){
            tiles[x][y].setCleared(true);
            Tile tile = new Tile(x, y, TileENUM.EMPTY);
            checked.add(tile);

            //To make sure more tiles than intended are revealed, we must check to see if the tile borders a bomb.
            for(int col = x-1; col <= x+1; col++){
                for(int row = y-1; row <= y+1; row++){
                    if(!(col < 0 || row < 0) && !(col > cols-1 || row > rows-1)){
                        if(tiles[col][row].getType() == TileENUM.BOMB){
                            nearBomb = true;
                        }
                    }
                }
            }

            //For every tile around the current tile, reveal the tile if it's not a bomb and recursively call this method with neighbouring tiles of bomb count 0.
            for(int col = x-1; col <= x+1; col++){
                for(int row = y-1; row <= y+1; row++){
                    if(!(col < 0 || row < 0) && !(col > cols-1 || row > rows-1)){

                        Tile newTile = new Tile(col, row, TileENUM.EMPTY);


                        if(!nearBomb){
                            tiles[col][row].setCleared(true);
                        }

                        if(calcBombs(col, row) == 0 && !checked.contains(newTile)){
                            autoClear(col, row);
                        }

                    }
                }
            }
        }
    }

//    private void autoClear(int x, int y){
//        boolean nearBomb = false;
//        if(tiles[x][y].getType() != TileENUM.BOMB){
//            tiles[x][y].setCleared(true);
//            Tile tile = new Tile(x, y, TileENUM.EMPTY);
//            checked.add(tile);
//
//            //To make sure more tiles than intended are revealed, we must check to see if the tile borders a bomb.
//            for(int col = x-1; col <= x+1; col++){
//                for(int row = y-1; row <= y+1; row++){
//                    if(!(col < 0 || row < 0) && !(col > cols-1 || row > rows-1)){
//                        if(tiles[col][row].getType() == TileENUM.BOMB){
//                            nearBomb = true;
//                        }
//                    }
//                }
//            }
//
//            //For every tile around the current tile, reveal the tile if it's not a bomb and recursively call this method with neighbouring tiles of bomb count 0.
//            for(int col = x-1; col <= x+1; col++){
//                for(int row = y-1; row <= y+1; row++){
//                    if(!(col < 0 || row < 0) && !(col > cols-1 || row > rows-1)){
//
//                        Tile newTile = new Tile(col, row, TileENUM.EMPTY);
//
////                        if(tiles[col][row].getType() == TileENUM.BOMB){
////                            nearBomb = true;
////                        }
//
//                        if(/*tiles[col][row].getType() != TileENUM.BOMB &&*/ !nearBomb){
//                            tiles[col][row].setCleared(true);
//                        }
//
//                        if(calcBombs(row, col) == 0 && !checked.contains(newTile)){
//                            autoClear(col, row);
//                        }
//
//                    }
//                }
//            }
//
//        }
//    }

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

    private boolean checkWin(){
        if(lost){
            return false;
        }
        Tile tile;
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
                tile = tiles[col][row]; // change back to row, col
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
