package regression;

import java.io.*;

public class Main {
//main method 
	public static void main(String[] args) throws IOException 

{ 

//construct neural network 
	Regression neural = new Regression(); 

//load training data 
		neural.loadTrainingData("trainingfile"); 

//set parameters of network 
		neural.setParameters(5, 1000, 2376, 0.9); //(middle nodes,iterations,seed,learning rate)

//train network' 
		neural.train(); 

//test network 
		neural.testData("inputfile", "outputfile"); 
		//neural.validate("validationfile");
		//neural.trainingError("trainingfile");
}
}
