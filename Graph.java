
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
//Program performs graph based clustering class 
public class Graph {
	/***************************•**********************************************/
	//Record class 
	private class Record {
		//attributes of record 
		private double[] attributes;
		//constructor of record
		private Record(double[] attributes)
		{
			this.attributes = attributes;
		}
	}
	/*************************************************************************/
	private int numberRecords;	//number of records
	private int numberAttributes;	//number of attributes
	private	double delta;	//neighbor threshold
	private	ArrayList<Record> records; //list of records
	private	int[][] matrix;	//adjacency matrix
	private	int[] clusters;	//clusters of records
	private double[][] centroids;  //array of centroids
	/*************************************************************************/
	//Constructor of clustering 
	public Graph()
	{
		//parameters are zero 
		numberRecords = 0; 
		numberAttributes = 0; 
		delta = 0;
		//lists are empty 
		records = null; 
		matrix = null; 
		clusters = null;
	}
	/*************************************************************************/
	//Method loads records from input file 
	public void load(String inputFile) throws IOException {
		Scanner inFile = new Scanner(new File(inputFile));
		//read number of records, attributes 
		numberRecords = inFile.nextInt(); 
		numberAttributes = inFile.nextInt();
		//empty list of records
		records = new ArrayList<Record>();
		//for each record
		for (int i = 0; i < numberRecords; i++)
		{
			//read attributes
			double[] attributes = new double[numberAttributes]; 
			for (int j = 0; j < numberAttributes; j++) 
				attributes[j] = inFile.nextDouble();
			//create record
			Record record = new Record(attributes);
			//add record to list 
			records.add(record);
		}
		inFile.close();
	}
	/*************************************************************************/
	//Method sets parameter of clustering 
	public void setParameter(double delta)
	{
		//set neighbor threshold 
		this.delta = delta;
	}
	/*************************************************************************/
	//Method performs clustering 
	public void cluster()
	{
		//create adjacency matrix of records 
		createMatrix();
		//initialize clusters of records 
		initializeClusters();
		//initial record index is 0 
		int index = 0;
		//initial cluster name is 0 
		int clusterName = 0;
		//while there are more records 
		while (index < numberRecords)
		{
			//if record does not have cluster name 
			if (clusters[index] == -1)
			{
				//assign cluster name to record and all records connected to it 
				assignCluster(index, clusterName);
				//find next cluster name 
				clusterName = clusterName + 1;
			}
			//go to next record 
			index = index + 1;
		}
	}
	/**********?**************************************************************/
	//Method creates adjacency matrix 
	private void createMatrix(){
		//allocate adjacency matrix
		matrix = new int[numberRecords][numberRecords];
		//entry (i, j) is 0 or 1 depending on i and j are neighbors or not 
		for (int i = 0; i < numberRecords; i++)
			for (int j = 0; j < numberRecords; j++)
				matrix[i][j] = neighbor(records.get(i), records.get(j));
	}
	/*************************************************************************/ 
	//Method decides whether two records are neighbors or not
	private int neighbor(Record u, Record v)
	{
		double distance = 0;
		//find euclidean distance between two records 
		for (int i = 0; i < u.attributes.length; i++)
			distance += (u.attributes[i] - v.attributes[i])*(u.attributes[i] - v.attributes[i]);
		distance = Math.sqrt(distance);
		//if distance is less than neighbor threshold records are neighbors, 
		//otherwise records are not neighbors 
		if (distance <= delta) 
			return 1;
		else
			return 0;
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
	/*************************************************************************/
	//Method assigns cluster name to a record and all records connected to it //using breadth first traversal
	private void assignCluster(int index, int clusterName)
	{
		//assign cluster name to record 
		clusters[index] = clusterName;
		//list used in traversal
		LinkedList<Integer> list = new LinkedList<Integer>();
		//put record into list 
		list.addLast(index);
		//while list has records 
		while (!list.isEmpty())
		{
			//remove first record from list 
			int i = list.removeFirst();
			//find neighbors of record which have no cluster names 
			for (int j = 0; j < numberRecords; j++)
				if (matrix[i][j] == 1 && clusters[j] == -1)
				{
					//assign cluster name to neighbor 
					clusters[j] = clusterName;
					//add neighbor to list 
					list.addLast(j);
				}
		}
	}
	/*************************************************************************/
	//Method writes records and their clusters to output file 
	public void display(String outputFile) throws IOException {
		PrintWriter outFile = new PrintWriter(new FileWriter(outputFile)); 
		computeCentroid();
		double error = 0;
		//for each record
		int max = Arrays.stream(clusters).max().getAsInt();
		System.out.println("Number Of Clusters:"+ (max+1));
		for (int k = 0; k < max+1; k++){
			outFile.println("\nCluster "+ (k+1) +" Records:");
			for (int i = 0; i < numberRecords; i++){
				if(clusters[i] == k){
					//write attributes of record
					for (int j = 0; j < numberAttributes; j++)
						outFile.print(records.get(i).attributes[j] + " ");
					error += computeError(records.get(i).attributes, centroids[clusters[i]]);
					//write cluster
					outFile .println(clusters [i ]+1);
				}
			}
		}
		outFile.close();
		System.out.format("Sum Squared error at end of clustering is %.2f for given %d records \n" ,error, numberRecords);		

	}
	/*************************************************************************/ 
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
	// Method to find the centroid of each cluster
	private void computeCentroid() {
		int max = Arrays.stream(clusters).max().getAsInt();
		centroids = new double[max+1][numberAttributes];
		for (int k = 0; k < max+1; k++){
			int numrecords = 0;
			for (int i = 0; i < numberRecords; i++){
				if(clusters[i] == k){
					numrecords++;
					for (int j = 0; j < numberAttributes; j++)
						centroids[k][j] += records.get(i).attributes[j];
				}
			}
			for (int j = 0; j < numberAttributes; j++)
				centroids[k][j] /= numrecords;
		}
	}
	/*************************************************************************/

}

