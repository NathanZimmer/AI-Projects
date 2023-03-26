/*
 * DESC: used to test the modified NearestNeighbor algorithm
 */

import java.io.*;
import java.util.*;

//Program tests nearest neighbor classifier in a specific application
public class NearestNeighborTester {
    // paramaters for algorithm
    private static final int NEIGHBORS = 2;

    // string params
    private static final String CLASSIFIED = "classifiedData.txt";
    private static String trainingFile;
    private static String testFile;
    private static String classifiedOutput;

    public static void main(String[] args) throws IOException {
        
        // getting user input for input files
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter path/name of training data:");
        trainingFile = scanner.nextLine();
        System.out.println("Enter path/name of test data:");
        testFile = scanner.nextLine();
        System.out.println("Enter path/name of the classified output file:");
        classifiedOutput = scanner.nextLine();
        scanner.close();
        

        // creating calssifier and loading training data
        NearestNeighbor classifier = new NearestNeighbor(NEIGHBORS);
        classifier.loadTrainingData(trainingFile);

        // calculating validation error
        classifier.validate();

        // classifing test data
        classifier.classifyData(testFile, CLASSIFIED);

        // converting to english
        convertClassFile(CLASSIFIED, classifiedOutput);
    }

    // converts classified file to text format for output file
    private static void convertClassFile(String inputFile, String outputFile) throws IOException {
        // setting up input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        // reading number of records and writing to output
        int numberRecords = inFile.nextInt();    
        outFile.println(numberRecords);

        // for each record, convert class number to word
        for (int i = 0; i < numberRecords; i++) {      
            int number = inFile.nextInt();                    
            String className = convertNumberToClass(number);
            outFile.println(className);
        }

        inFile.close();
        outFile.close();
    }

    // converts number to class name
    private static String convertNumberToClass(int number) {
        return number == 1 ? "one" : "zero";
    }
}