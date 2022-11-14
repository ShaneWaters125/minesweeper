package org.example;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Grid {

    private int cols, rows;
    private final Tile[][] tiles;
    private Scanner input = new Scanner(System.in);

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
            autoClear(x, y);
            System.out.println("");
        }
    }

    private void autoClear(int x, int y){
        boolean isBomb = false;
        if(tiles[x][y].getType() != TileENUM.BOMB){
            tiles[x][y].setCleared(true);
            Tile tile = new Tile(x, y, TileENUM.EMPTY);
            checked.add(tile);

            for(int col = x-1; col <= x+1; col++){
                for(int row = y-1; row <= y+1; row++){
                    if(!(col < 0 || row < 0) && !(col > cols-1 || row > rows-1)){

                        Tile newTile = new Tile(col, row, TileENUM.EMPTY);

                        if(tiles[col][row].getType() == TileENUM.BOMB){
                            isBomb = true;
                        }

                        if(/*tiles[col][row].getType() != TileENUM.BOMB &&*/ !isBomb){
                            tiles[col][row].setCleared(true);
                        }

                        if(calcBombs(row, col) == 0 && !checked.contains(newTile)){
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

    private int calcBombs(int col, int row){
        int bombTotal = 0;
        for(int x = col-1; x <= col+1; x++){
            for(int y = row-1; y <= row+1; y++){
                if(!(x < 0 || y < 0) && !(x > cols-1 || y > rows-1)){
                    if(tiles[y][x].getType() == TileENUM.BOMB){
                        bombTotal += 1;
                    }
                }
            }
        }
        return bombTotal;
    }

    public void printGrid(){
        Tile tile;
        int x, y;
        for(int col = 0; col <  cols ; col++){ //cols
            for(int row = 0; row < rows; row++){ //rows
                System.out.print("  ");
                tile = tiles[row][col];
                if(tile.isFlagged()){
                    System.out.print("F");
                }else{
                    if(tile.isCleared()){
                        System.out.print(calcBombs(col, row));
                    } else{
                        if(tile.getType() == TileENUM.EMPTY){
                            System.out.print("-");
                        } else{
                            System.out.print("B");
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
