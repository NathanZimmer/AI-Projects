/* 
 * DESC: uses MinMax function with depth limit and alpha-beta pruning to quickly play a game
 * GAME: the game is player on a nxn board. The player and computer take turns placing on the board. The goal is to get as many in a row as possible
 *       two points are received for each two in a row on the board. Three points are recieved for each three in a row. These values can stack (Ex: three in a row gives you 7 points because there is one three and two twos)
 *       the game does not account for illegal moves
 */

import java.util.LinkedList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class Game {
    private final char EMPTY = ' ';    // empty slot
    private final char COMPUTER = 'X'; // computer
    private final char PLAYER = '0';   // player
    private final int MIN = 0;         // min level
    private final int MAX = 3;         // max level
    private final int DEPTH_LIM = 10;

    private File otp;
    private FileWriter fw;
    private BufferedWriter bw;

    // Board class (inner class)
    private class Board {
        private char[][] array;  // board array

        private Board(int size) {
            array = new char[size][size]; // creating array
                                             
            // filling the array with empty slots
            for (int i = 0; i < size; i++)         
                for (int j = 0; j < size; j++)
                    array[i][j] = EMPTY;
        }
    }

    private Board board; // cuurent game board
    private int size;    // size of board
    
    public Game(int size, String otp) throws IOException {
        this.size = size;
        this.board = new Board(size);              
        this.otp = new File(otp);
        
        // setting up for output
        fw = new FileWriter(this.otp);
        bw = new BufferedWriter(fw);
    }

    // plays game
    public void play() throws IOException {
        // computer and player take turns
        while (true)                             
        {
            bw.write("Player move: ");
            board = playerMove(board);

            if (full(board)) {
                break;
            }

            bw.write("Computer move:\n");
            board = computerMove(board);

            if (full(board)) {
                break;
            }
        }

        // calcuates the scores for displaying
        int computerScore = evaluate(board, COMPUTER);
        int playerScore = evaluate(board, PLAYER);

        // displaying
        String score = String.format("player scored: %d    computer scored: %d", playerScore, computerScore);
        System.out.println(score);
        bw.write(score + "\n");
        
        // determining who wins and displaying
        if (playerScore > computerScore) {
            System.out.println("player wins!!");
            bw.write("player wins!!");
        }
        else if (playerScore < computerScore) {
            System.out.println("Computer wins");
            bw.write("Computer wins");
        }
        else {
            System.out.println("Tie game!");
            bw.write("Tie game!");
        }
        bw.close();
        fw.close();
    }

    // lets the player make a move. does not account for illegal moves
    private Board playerMove(Board board) throws IOException {
        // prompt player and read input
        System.out.print("Player move: ");         
        Scanner scanner = new Scanner(System.in); 
        int i = scanner.nextInt();
        int j = scanner.nextInt();

        // places player symbol and displays board
        board.array[i][j] = PLAYER;                
        bw.write(i + " " + j + "\n");
        displayBoard(board);           

        return board;
    }

    // determines computer's move
    private Board computerMove(Board board) throws IOException {                                             
        // generates children of board
        LinkedList<Board> children = generate(board, COMPUTER);

        int maxIndex = -1;
        int maxValue = Integer.MIN_VALUE;
        // finds child with largest minmax value
        for (int i = 0; i < children.size(); i++) {
            int currentValue = minmax(children.get(i), MIN, 0, 0, 0);
            if (currentValue > maxValue) {
                maxIndex = i;
                maxValue = currentValue;
            }
        }

        // chooses this child as our next move
        Board result = children.get(maxIndex);     
                                                
        // prints move
        System.out.println("Computer move:"); 
        displayBoard(result);                     

        return result;
    }

    // computes minmax value of a board
    private int minmax(Board board, int level, int depth, int alpha, int beta)
    {
        int computerScore = evaluate(board, COMPUTER);
        int playerScore = evaluate(board, PLAYER);

        // returns the value of the terminal board
        if (computerScore > playerScore)
            return compareScore(computerScore, playerScore);
        else if (computerScore < playerScore) 
            return compareScore(playerScore, computerScore);
        else {
            // if we hit the depth limit return value of the furthest board we reached
            if (depth == DEPTH_LIM) {
                if (computerScore > playerScore)
                    return compareScore(computerScore, playerScore);
                else if (computerScore < playerScore) 
                    return compareScore(playerScore, computerScore);
            }
            // if board is at max level
            if (level == MAX) {
                // generates children of board
                LinkedList<Board> children = generate(board, COMPUTER);
                int maxValue = Integer.MIN_VALUE;
                
                // finds maximum of minmax value of children. Uses alpha-beta pruning
                for (int i = 0; i < children.size(); i++) {                
                    int currentValue = minmax(children.get(i), MIN, depth + 1, alpha, beta);

                    if (currentValue > maxValue)
                        maxValue = currentValue;
                    if (maxValue >= beta) 
                    return maxValue;
                    if (maxValue > alpha) 
                    alpha = maxValue;
                }
                return maxValue;             
            }
            // if board is at min level
            else {                     
                // generates children of board
                LinkedList<Board> children = generate(board, PLAYER);
                int minValue = Integer.MAX_VALUE;

                // finds minimum of minmax values of children. Uses alpha-beta pruning
                for (int i = 0; i < children.size(); i++) {
                    int currentValue = minmax(children.get(i), MAX, depth + 1, alpha, beta);

                    if (currentValue < minValue)
                        minValue = currentValue;
                    if (minValue <= alpha) 
                    return minValue;
                    if (minValue < beta) 
                    beta = minValue;
                }

                return minValue;
            }
        }
    }

    // returns a value depending on how much higher x is than y
    private int compareScore(double x, double y) {
        // if x has a score more than double y
        if (x / 2 >= y)
            return 3;
        // if x has a score more than a quater better than y
        else if (x / 1.33 >= y)
            return 2;
        // if x has a score just a little better y
        else
            return 1;
    }

    // generates children of board using a symbol
    private LinkedList<Board> generate(Board board, char symbol) {
        LinkedList<Board> children = new LinkedList<Board>();

        for (int i = 0; i < size; i++)
            // goes through board
            for (int j = 0; j < size; j++)   
                // if slot is empty, add symbols and create child
                if (board.array[i][j] == EMPTY) {                                  
                    Board child = copy(board);     
                    child.array[i][j] = symbol;    
                    children.addLast(child);
                }

        return children;
    }                 

    // checks the score of a particular child board. Adds up the score of the 
    private int evaluate(Board board, char symbol) {
        int score = 0;
        for (int i = 0; i < size; i++) {
            score += checkRow(board, i, symbol);
            score += checkColumn(board, i, symbol);
        }
        
        return score;                          
    }

    // checks the score of a particular row
    private int checkRow(Board board, int i, char symbol) {
        int score = 0;
        int sequence = 0;
        for (int j = 0; j < size; j++) {
            sequence++;
            if (board.array[i][j] != symbol)
               sequence = 0;
            
            if (sequence >= 2) {
                score += 2;
            }
            if (sequence >= 3) {
                score += 3;
            }
        }

        return score;
    }

    // checks the score of a particular column
    private int checkColumn(Board board, int i, char symbol) {
        int score = 0;
        int sequence = 0;
        for (int j = 0; j < size; j++) {
            sequence++;
            if (board.array[j][i] != symbol)
               sequence = 0;
            
            if (sequence >= 2) {
                score += 2;
            }
            if (sequence >= 3) {
                score += 3;
            }
        }

        return score;
    }

    // checks whether a board is full
    private boolean full(Board board) {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (board.array[i][j] == EMPTY)
                   return false;

        return true;
    }

    // makes copy of a board
    private Board copy(Board board) {
        Board result = new Board(size);      

        for (int i = 0; i < size; i++)       
            for (int j = 0; j < size; j++)
                result.array[i][j] = board.array[i][j];

        return result;                       
    }

    // displays a board
    private void displayBoard(Board board) throws IOException
    {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(board.array[i][j]);
                bw.write(board.array[i][j]);
            }
            System.out.println();
            bw.write("\n");
        }
        bw.write("\n");
    }
}