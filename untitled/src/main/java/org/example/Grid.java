package org.example;

import java.util.Random;
import java.util.Scanner;

public class Grid {

    private int cols, rows;
    private final Tile[][] tiles;
    private Scanner input = new Scanner(System.in);

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
        printGrid();
        System.out.println("Type C: Clear Tile, F: Flag Tile");
        String decision = input.nextLine();
        switch (decision.toUpperCase()) {
            case "F" -> flagTile();
            case "C" -> clearTile();
            default -> System.out.println("");
        }
    }

    private void flagTile(){
        System.out.println("Enter the tiles x coordinate you would like to flag: ");
        int x = input.nextInt();
        System.out.println("Enter the tiles y coordinate you would like to flag: ");
        int y = input.nextInt();
        tiles[x][y].setFlagged(true);
    }

    private void clearTile(){
        System.out.println("Enter the tiles x coordinate you would like to clear: ");
        int x = input.nextInt();
        System.out.println("Enter the tiles y coordinate you would like to clear: ");
        int y = input.nextInt();

        Tile tile = tiles[x][y];

        if(tile.getType() == TileENUM.BOMB){
            System.out.println("GAME OVER!");
        }else{
            tile.setCleared(true);
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

    private boolean checkWin(){
        Tile tile;
        for(int col = 0; col < cols; col++){
            for(int row = 0; row < rows; row++){
                tile = tiles[row][col];
                if(tile.getType() == TileENUM.EMPTY && !tile.isCleared()){
                    return true;
                }
            }
        }
        return false;
    }

    public void printGrid(){
        Tile tile;
        for(int col = 0; col < cols; col++){
            for(int row = 0; row < rows; row++){
                tile = tiles[row][col];
                System.out.print("(" + tile.getX() + ", " + tile.getY() + ")");
                if(tile.isFlagged()){
                    System.out.print(" F ");
                }else{
                    if(tile.isCleared()){
                        System.out.print(" C ");
                    } else{
                        if(tile.getType() == TileENUM.EMPTY){
                            System.out.print(" X ");
                        } else{
                            System.out.print(" B ");
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
