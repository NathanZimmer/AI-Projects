/* 
 * DESC: uses the nearest neighbor algorithm to determine the likelihood of someone defaulting on a loan
 * PARAMS: credit score, income, age, sex, marriage status
 * CLASSES: low, medium, high, undetermined
*/

import java.io.*;
import java.util.*;

public class NearestNeighborTester {
    // paramaters for algorithm
    private static final int NEIGHBORS    = 6;  // given to the algorithm as the k value
    private static final int CREDIT_START = 500;
    private static final int CREDIT_END   = 900;
    private static final int INCOME_START = 30;
    private static final int INCOME_END   = 90;
    private static final int AGE_START    = 30;
    private static final int AGE_END      = 80;

    // string params 
    private static final String TRAINING_FILE = "trainingFile.txt";
    private static final String TEST_FILE = "testFile.txt";
    private static final String CLASSIFIED = "classifiedData.txt";
    private static String origTrainingFile;
    private static String origTestFile;
    private static String classifiedOutput;

    // main method
    public static void main(String[] args) throws IOException {
        // getting user input for input files
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter path/name of training data:");
        origTrainingFile = scanner.nextLine();
        System.out.println("Enter path/name of test data:");
        origTestFile = scanner.nextLine();
        System.out.println("Enter path/name of the classified output file:");
        classifiedOutput = scanner.nextLine();
        scanner.close();

        // preprocessing files - normalzing and converting words to numbers
        convertTrainingFile(origTrainingFile, TRAINING_FILE);
        convertTestFile(origTestFile, TEST_FILE); 

        // creating new classifier, loading data, and setting params
        NearestNeighbor classifier = new NearestNeighbor(NEIGHBORS);
        classifier.loadTrainingData(TRAINING_FILE);

        // running validation function
        classifier.validate();

        // classifying test data 
        classifier.classifyData(TEST_FILE, CLASSIFIED);

        // converting from class numbers to corresponding words
        convertClassFile(CLASSIFIED, classifiedOutput);
    }

    // converts and normalizes original training file and stores it into the file we use in the algo
    private static void convertTrainingFile(String inputFile, String outputFile) throws IOException {
        // setting up input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        // gets number of records, attributes, and classes
        int numberRecords = inFile.nextInt();
        int numberAttributes = inFile.nextInt();    
        int numberClasses = inFile.nextInt();

        // write number of records, attributes, classes to output
        outFile.println(numberRecords + " " + numberAttributes + " " + numberClasses);

        // for each record
        for (int i = 0; i < numberRecords; i++) {                         
            // convert credit score
            double creditScore = inFile.nextDouble();                      
            creditScore = normalize(creditScore, CREDIT_START, CREDIT_END);
            outFile.print(creditScore + " ");

            // convert income
            double income = inFile.nextDouble();                  
            income = normalize(income, INCOME_START, INCOME_END);
            outFile.print(income + " ");

            // convert age
            double age = inFile.nextDouble();
            age = normalize(age, AGE_START, AGE_END);
            outFile.print(age + " ");

            // convert sex
            String sex = inFile.next();
            int sexId = sex.equals("male") ? 0 : 1;
            outFile.print(sexId + " ");

            // convert marriage status
            String status = inFile.next();
            double statusId = convertMarried(status);
            outFile.print(statusId + " ");

            // convert class name 
            String className = inFile.next();                  
            int classNumber = convertClassToNumber(className);
            outFile.println(classNumber);
        }

        inFile.close();
        outFile.close();
    }

    // converts test file to numerical format
    private static void convertTestFile(String inputFile, String outputFile) throws IOException {
        // setting up input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        // reading number of records and writing to output
        int numberRecords = inFile.nextInt();    
        outFile.println(numberRecords);

        // for each record
        for (int i = 0; i < numberRecords; i++) {                         
            // convert credit score
            double creditScore = inFile.nextDouble();                      
            creditScore = normalize(creditScore, CREDIT_START, CREDIT_END);
            outFile.print(creditScore + " ");

            // convert income
            double income = inFile.nextDouble();                  
            income = normalize(income, INCOME_START, INCOME_END);
            outFile.print(income + " ");

            // convert age
            double age = inFile.nextDouble();
            age = normalize(age, AGE_START, AGE_END);
            outFile.print(age + " ");

            // convert sex
            String sex = inFile.next();
            int sexId = sex.equals("male") ? 0 : 1;
            outFile.print(sexId + " ");

            // convert marriage status
            String status = inFile.next();
            double statusId = convertMarried(status);
            outFile.println(statusId + " ");
        }

        inFile.close();
        outFile.close();
    }

    // converts classified file to text format
    private static void convertClassFile(String inputFile, String outputFile) throws IOException {
        // input and output files
        Scanner inFile = new Scanner(new File(inputFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile));

        // gets and writes number of records
        int numberRecords = inFile.nextInt();    
        outFile.println(numberRecords);

        // for each record
        for (int i = 0; i < numberRecords; i++) {      
            // converts class number
            int number = inFile.nextInt();                    
            String className = convertNumberToClass(number);
            outFile.println(className);
        }

        inFile.close();
        outFile.close();
    }

    // normalizes given input values based on given range
    private static double normalize(double x, double min, double max) {
        return (x - min) / (max - min);
    }

    // converts from marital status to number
    private static double convertMarried(String status) {
        switch(status) {
            case "married":
                return 0.0;
            case "single":
                return 0.5;
        }
        return 1.0;
    }

    // converts class name to number
    private static int convertClassToNumber(String className) {
        switch(className) {
            case "low":
                return 1;
            case "medium":
                return 2;
            case "high":
                return 3;
        }
        return 4;
    }

    // converts number to class name
    private static String convertNumberToClass(int number) {
        switch(number) {
            case 1:
                return "low";
            case 2:
                return "medium";
            case 3:
                return "high";
        }
        return "undetermined";
    }
}
