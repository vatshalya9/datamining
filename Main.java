package bayes;

import java.io.*; 

//********************************************************************

//Program tests Bayes classifier 
public class Main 

{ 

//main method 
public static void main(String[] args) throws IOException 

{ 

//construct bayes classifier 
	Bayes b = new Bayes(); 

//load training data
 b.loadTrainingData("trainingfile"); 

//compute probabilities 
b.computeProbability(); 	 
//classify data 
b.classifyData("testfile", "classifiedfile"); 
//b.validate("trainingfile","classifier","validationfile","newTrainFile");

} 
}

