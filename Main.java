
import java.io.*;
//Program performs graph based clustering
public class Main {
	//main method
	public static void main(String[] args) throws IOException
	{
		//create clustering object
		Graph clustering = new Graph();
		//load data records
		clustering.load("inputfile");
		//set parameter
		clustering.setParameter(6);
		//perform clustering
		clustering.cluster();
		//display records and clusters
		clustering.display("outputfile");
	}
}

