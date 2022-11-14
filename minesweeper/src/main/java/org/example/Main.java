package org.example;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        initGame();
    }

    private static void initGame(){
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to Minesweeper!");
        System.out.println("How many columns would you like?:");
        int cols = input.nextInt();
        System.out.println("How many rows would you like?:");
        int rows = input.nextInt();

        Grid grid = new Grid(cols, rows);
    }
}
