/*
 * DESC: this is a modified version of the nearest neighbor program that uses binary distance to identify images made by 1s and 0s
 */

import java.io.*;
import java.util.*;

public class NearestNeighbor {
     // Record class (inner class)
     private class Record {
        private int[][] attributes; // attributes of the record (credit score, income, age, sex, status)  
        private int className;       // class of the record

        private Record(int[][] attributes, int className) {
            this.attributes = attributes; 
            this.className = className;  
        }
    }

    private int numberRecords;         // number of training records   
    private int numberAttributes;      // number of attributes   
    private int numberClasses;         // number of classes
    private int numberNeighbors;       // number of nearest neighbors
    private ArrayList<Record> records; // list of training records

    public NearestNeighbor(int k) {
        numberNeighbors = k;
    }

    // loads data from training file
    public void loadTrainingData(String trainingFile) throws IOException {
        Scanner inFile = new Scanner(new File(trainingFile));

        // reads number of records, attributes, classes
        numberRecords = inFile.nextInt();
        numberAttributes = inFile.nextInt();
        numberClasses = inFile.nextInt();

        // creates list of records
        records = new ArrayList<Record>();        

        // for each record
        for (int i = 0; i < numberRecords; i++) {
            // create attribute array
            int[][] attributeArray = new int[numberAttributes][numberAttributes];
                                    
            // read attribute values
            for (int j = 0; j < numberAttributes; j++)   
                for (int k = 0; k < numberAttributes; k++)
                    attributeArray[j][k] = inFile.nextInt();

            // read class name
            int className = inFile.nextInt();

            // create record and add to list
            Record record = new Record(attributeArray, className);
            records.add(record);
        }

        inFile.close();
    }

    // reads records from test file, determines their classes, and writes classes to classified file
    public void classifyData(String testFile, String classifiedFile) throws IOException {
        Scanner inFile = new Scanner(new File(testFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(classifiedFile));

        // reads number of records
        int numberRecords = inFile.nextInt();

        // writes number of records
        outFile.println(numberRecords);

        // for each record
        for (int i = 0; i < numberRecords; i++) {
            // create attribute array
            int[][] attributeArray = new int[numberAttributes][numberAttributes];
                                     
            // read attribute values
            for (int j = 0; j < numberAttributes; j++)   
                for (int k = 0; k < numberAttributes; k++)
                    attributeArray[j][k] = inFile.nextInt();

            // find class of attributes
            int className = classify(attributeArray);

            // write class name
            outFile.println(className);
        }

        inFile.close();
        outFile.close();
    }      

    // determines the class of a set of attributes
    private int classify(int[][] attributes) {   
        double[] distance = new double[numberRecords];
        int[] id = new int[numberRecords];

        // finds distances between attributes and all records
        for (int i = 0; i < numberRecords; i++) {
            double rowDisances = 0;
            // for each row of each "image" we get the binary distance and add them together to get the binary distance of the entire nxn image
            for (int j = 0; j < numberAttributes; j++) {
                rowDisances += distance(attributes[j], records.get(i).attributes[j]);
            }
            distance[i] = rowDisances;
            id[i] = i;
        }

        // finds nearest neighbors
        nearestNeighbor(distance, id);

        // finds majority class of nearest neighbors
        int className = majority(id);

        // returns class
        return className;
    }

    // finds the nearest neighbors
    private void nearestNeighbor(double[] distance, int[] id) {
        // sorts distances and choose nearest neighbors
        for (int i = 0; i < numberNeighbors; i++)
            for (int j = i; j < numberRecords; j++)
                if (distance[i] > distance[j]) {
                    double tempDistance = distance[i];
                    distance[i] = distance[j];
                    distance[j] = tempDistance;

                    int tempId = id[i];
                    id[i] = id[j];
                    id[j] = tempId;
                }
    }

    // finds the majority class of nearest neighbors
    private int majority(int[] id) {
        double[] frequency = new double[numberClasses];

        //class frequencies are zero initially
        for (int i = 0; i < numberClasses; i++)
            frequency[i] = 0;

        //each neighbor contributes 1 to its class
        for (int i = 0; i < numberNeighbors; i++)
            frequency[records.get(id[i]).className - 1] += 1;

        //find majority class
        int maxIndex = 0;                         
        for (int i = 0; i < numberClasses; i++)   
            if (frequency[i] > frequency[maxIndex])
               maxIndex = i;

        return maxIndex + 1;
    }

    // finds binary distance between two points
    private double distance(int[] u, int[] v) {
        double mismatch = 0;
        for (int i = 0; i < u.length; i++) {
            if (u[i] != v[i]) mismatch++;
        }              

        return mismatch / u.length;
    }

    // validates classifier using training file and leave one out method
    public void validate() {
        // gets number of records
        int numberRecords = records.size();

        // initially zero errors
        int numberErrors = 0;

        // removing each record from the list and adding them back in after we have validated them
        this.numberRecords--;
        for (int i = 0; i < numberRecords; i++) {
        Record currentRec = records.remove(i);
        int[][] attributeArray = currentRec.attributes;

        int predictedClass = classify(attributeArray);

        // errror if predicted and actual classes do not match
        if (predictedClass != currentRec.className)               
            numberErrors++;

        // putting record back into the same spot it was removed from
        records.add(i, currentRec);
        }
        this.numberRecords++;

        // finds and prints error rate
        double errorRate = 100.0*numberErrors/numberRecords;
        System.out.println("validation error: " + errorRate + "%");
    }
}