package org.example;

public class Tile {

    private int x, y;
    private TileENUM type;
    private boolean isFlagged, isCleared;

    public Tile(int x, int y, TileENUM type){
        this.x = x;
        this.y = y;
        this.type = type;
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
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                ", type=" + type +
                '}';
    }
}
