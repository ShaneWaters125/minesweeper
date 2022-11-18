package org.example;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        initGame();
    }

    /**
     * Initialises the game & grid.
     */
    private static void initGame(){
        try{
            Scanner input = new Scanner(System.in);
            System.out.println("Welcome to Minesweeper!");
//        System.out.println("How big would you like the square grid?:");
            System.out.println("How many columns would you like? (1-50): ");
            int rows = input.nextInt();
            System.out.println("How many rows would you like? (1-50): ");
            int cols = input.nextInt();
            if(rows > 0 && cols > 0 && rows < 51 && cols < 51){
                Grid grid = new Grid(cols, rows);
            }else{
                System.out.println("Invalid Input! Try Again.");
                initGame();
            }
        }catch (Exception e){
            System.out.println("Invalid Input! Try Again.");
            initGame();
        }
    }
}
