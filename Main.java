import java.io.*;
//Program performs k-means clustering
public class Main {
	//main method
	public static void main(String[] args) throws IOException
	{
		//create clustering object
		Kmeans k = new Kmeans();
		//load data records
		k.load("inputfile");
		//set parameters
		k.setParameters(3, 4975);
		//perform clustering
		k .cluster();
		//cluster records to output file and display clusters
		k.clusterOutput("outputfile");
	}
}
