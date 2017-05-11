package near;
import java.io.*; 

//Program tests nearest neighbor classifier 
public class Main 
{ 

	//main method 
	public static void main(String[] args) throws IOException 
	{ 

		//construct nearest neighbor classifier 
		NearestNeighbor classifier = new NearestNeighbor(); 

		//load training data 
		classifier.loadTrainingData("trainingfile"); 
		
		classifier.classifyData("testfile","classifiedfile");

		//classifier.trainingError("trainingfile","outputfile"); 
		//classifier.validate("validationfile","outtextfile");
		//classifier.crosValidation("trainingfile");

	}
}


