
package neural;
import java.io.*; 

//Program tests neural network 
public class Main
{ 

//main method 
	public static void main(String[] args) throws IOException 

{ 

//construct neural network 
		Neural network = new Neural(); 

//load training data 
		network.loadTrainingData("convertedtrainingfile"); 

//set parameters of network 
		network.setParameters(5, 1000, 2376, 0.9); //(middle nodes,iterations,seed,learning rate)

//train network' 
		network.train(); 

//test network 
		network.testData("inputfile", "outputfile"); 
		//network.validate("validationfile");
		network.trainingError("trainingfile");
}
}
