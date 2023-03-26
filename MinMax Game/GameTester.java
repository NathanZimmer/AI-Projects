/*
 * DESC: this file tests the MinMax game playing function. It takes board size input and file output. A copy of the game is written to the output file
 */

import java.io.IOException;
import java.util.Scanner;

public class GameTester {
   public static void main(String[] args)
   {
      // getting board and file input
      Scanner scanner = new Scanner(System.in);
      System.out.println("Enter size of board for game: ");
      int size = scanner.nextInt();
      System.out.println("Enter name of output file");
      String otp = scanner.next();

      // playing tic-tac game
	   try {
         Game t = new Game(size, otp);
         t.play();
      } catch (IOException e) {
         e.printStackTrace();
      }

      scanner.close();
   }
}
