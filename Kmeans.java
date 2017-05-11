import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;


public class Kmeans {

	private int numberRecords;
	private int numberAttributes; 
	private int numberClusters;

	private double[][] records; //array of records
	private double[][] centroids;  //array of centroids
	private int[] clusters;  //clusters of records
	private Random rand; //random number generator  
	/**********************************************************************/
	//Constructor of Kmeans class 
	public Kmeans()
	{
		//parameters are zero 
		numberRecords = 0; 
		numberAttributes = 0; 
		numberClusters = 0;
		//arrays are empty 
		records = null; 
		centroids = null; 
		clusters = null; 
		rand = null;
	}
	/*********************************************************************/
	//Method loads records from input file 
	public void load(String inputFile) throws IOException {
		Scanner inFile = new Scanner(new File(inputFile));
		//read number of records, attributes 
		numberRecords = inFile.nextInt(); 
		numberAttributes = inFile.nextInt();
		//create array of records
		records = new double[numberRecords][numberAttributes];
		//for each record
		for (int i = 0; i < numberRecords; i++)
		{
			//read attributes
			for (int j = 0; j < numberAttributes; j++) 
				records[i][j] = inFile.nextDouble();
		}
		inFile.close();
	}
	/*********************************************************************/
	//Method sets parameters of clustering
	public void setParameters(int numberClusters, int seed)
	{
		//set number of clusters
		this.numberClusters = numberClusters;
		//create random number generator with seed 
		this.rand = new Random(seed);
	}
	/*************************************************************************/
	//Method performs k-means clustering 
	public void cluster()
	{
		//initialize clusters of records 
		initializeClusters();
		//initialize centroids of clusters 
		initializeCentroids();
		//stop condition has not been reached 
		boolean stopCondition = false;
		//while stop condition is not reached 
		int cycle = 1;
		while (!stopCondition)
		{
			//assign clusters to records
			int clusterChanges = assignClusters();
			//update centroids of clusters 
			System.out.print("\nAfter Cycle "+cycle+++":");
			updateCentroids();
			//stop condition is reached if no records changed clusters 
			stopCondition = (clusterChanges == 0);
		}
	}
	/*************************************************************************/
	//Method initializes clusters of records 
	private void initializeClusters()
	{
		//create array of cluster labels 
		clusters = new int[numberRecords];
		//assign cluster -1 to all records 
		for (int i = 0; i < numberRecords; i++) 
			clusters[i] = -1;
	}
	/***************************************************?*********************/
	//Method initializes centroids of clusters 
	private void initializeCentroids()
	{
		//create array of centroids
		centroids = new double[numberClusters][numberAttributes];
		System.out.print("Initial Centroid:");
		//for each cluster
		for (int i = 0; i < numberClusters; i++)
		{
			System.out.print("\nCluster "+(i+1)+":");
			//randomly pick a record
			int index = rand.nextInt(numberRecords);
			//use record as centroid
			for (int j = 0; j < numberAttributes; j++) {
				centroids[i][j] = records[index][j];
				System.out.print(records[index][j]+ " ");
			}
		}
	}
	/*************************************************************************/
	//Method assigns clusters to records 
	private int assignClusters()  {
		int clusterChanges = 0;
		//go thru records and assign clusters to them 
		for (int i = 0; i < numberRecords; i++)
		{
			//find distance between record and first centroid 
			double minDistance = distance(records[i], centroids[0]); 
			int minlndex = 0;
			//go thru centroids and find closest centroid 
			for (int j = 0; j < numberClusters; j++)
			{
				//find distance between record and centroid
				double distance = distance(records[i], centroids[j]);
				//if distance is less than minimum, update minimum 
				if (distance < minDistance)
				{
					minDistance = distance; minlndex = j;
				}
			}
			//if closest cluster is different from current cluster 
			if (clusters[i] != minlndex)
			{
				//change cluster of record 
				clusters[i] = minlndex;
				//keep count of cluster changes 
				clusterChanges++;
			}
		}
		//return number of cluster changes 
		return clusterChanges;
	}
	/*******************************************************************?******/
	//Method updates centroids of clusters 
	private void updateCentroids()
	{
		//create array of cluster sums and initialize
		double[][] clusterSum = new double[numberClusters][numberAttributes];
		for (int i = 0; i < numberClusters; i++)
			for (int j = 0; j < numberAttributes; j++) 
				clusterSum[i][j] = 0;
		//create array of cluster sizes and initialize 
		int[] clusterSize = new int[numberClusters]; 
		for (int i = 0; i < numberClusters; i++) 
			clusterSize[i] = 0;
		//for each record
		for (int i = 0; i < numberRecords; i++)
		{
			//find cluster of record 
			int cluster = clusters[i];
			//add record to cluster sum
			clusterSum[cluster] = sum(clusterSum[cluster], records[i]);
			//increment cluster size 
			clusterSize[cluster] += 1;
		} 
		//find centroid of each cluster 
		for (int i = 0; i < numberClusters; i++){
			System.out.print("\nCentroid "+(i+1)+":");
			centroids[i] = scale(clusterSum[i], 1.0/clusterSize[i]);
		}
	}
	/*************************************************************************/
	//Method finds distance between two records 
	private double distance(double[] u, double[] v)
	{
		double sum = 0;
		//find euclidean distance square between two records 
		for (int i = 0; i < u.length; i++)
			sum += (u[i] - v[i])*(u[i] - v[i]);
		return sum;
	}
	/*************************************************************************/
	//Method finds sum of two records
	private double[] sum(double[] u, double[] v)
	{
		double[] result = new double[u.length];
		//add corresponding attributes of records 
		for (int i = 0; i < u.length; i++) result[i] = u[i] + v[i];
		return result;
	}
	/*************************************************************************/
	//Method finds scaler multiple of a record 
	private double[] scale(double[] u, double k)
	{
		double[] result = new double[u.length];
		//multiply attributes of record by scaler 
		for (int i = 0; i < u.length; i++) {
			result[i] = u[i]*k;
			System.out.print(String.format("%.1f",result[i])+ " ");
		}

		return result;
	}
	/*************************************************************************/
	//Method clusters records and their clusters to output file and print on console
	public void clusterOutput(String outputFile) throws IOException {
		PrintWriter outFile = new PrintWriter(new FileWriter(outputFile)); 
		double error = 0;
		//for each record
		for (int k = 0; k < numberClusters; k++){
			outFile.println("\nCluster "+ (k+1) +" Records:");
			for (int i = 0; i < numberRecords; i++){
				if(clusters[i] == k){
					//write attributes of record 
					for (int j = 0; j < numberAttributes; j++)
						outFile.print(String.format("%.0f", records[i][j]) + " ");
					error += computeError(records[i], centroids[clusters[i]]);
					//write cluster label 
					outFile.println(clusters[i]+1);
				}
			}
		}
		System.out.print("\nCentroids at end of Clustering:");
		for (int k = 0; k < numberClusters; k++){
			System.out.print("\nCentroid "+(k+1)+":");
			for (int j = 0; j < numberAttributes; j++){
				System.out.print(String.format("%.1f",centroids[k][j])+ " ");
			}
		}
		
		System.out.format("\nSum Squared error at end of clustering is %.2f for given %d records \n" ,error, numberRecords);		
		outFile.close();
	}
	/*******************************************************************/
	// Method to find the root mean square error
	private double computeError(double[] actualOutput, double[] centroid) {
		double error = 0;
		// sun of square of errors
		for (int i = 0; i < actualOutput.length; i++)
			error += Math.pow(actualOutput[i] - centroid[i], 2);

		// root mean square error
		return Math.sqrt(error / actualOutput.length);
	}
	/*************************************************************************/
}

