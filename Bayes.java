package bayes;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class Bayes {

	// Training record class
	private class Record {
		private int[] attributes; // attributes of records
		private int className; // class of record

		// constructor of record
		private Record(int[] attributes, int className) {
			this.attributes = attributes;// assign attributes
			this.className = className; // assign class name

		}

	}

	private ArrayList<Record> records; // list of training records
	// classname
	private int numberRecords; // number of training records
	private int numberAttribute; // number of attributes
	private int numberClasses; // number of classes
	private int[] attributeValues; // number of attribute values
	double[][][] table; // conditional probabilities
	double[] classTable; // class probabilities

	// constructor of the class
	public Bayes() {

		records = null; // initialize records to empty;
		numberRecords = 0; // number of records,attributes,
							// classes are zero
		numberAttribute = 0;
		numberClasses = 0;
		attributeValues = null; // nearest neighbors,majority rule has
								// no values
		table = null; // set records and attributes values to empty
		classTable = null; // set probability tables to empty

	}
	private void trimClassifier(String trainingFile, String trainingFileOut)
			throws IOException {

		Scanner inFile = new Scanner(new File(trainingFile));
		PrintWriter outFile = new PrintWriter(new FileWriter(trainingFileOut));
		// read number of records,attributes,majority rule
		numberRecords = inFile.nextInt();
		numberAttribute = inFile.nextInt();
		numberClasses = inFile.nextInt();
		outFile.println(numberRecords);
		inFile.nextLine();
		inFile.nextLine();
		String label = null;
		// for each record
		for (int i = 0; i < numberRecords; i++) {

			// read attributes and convert them to numerical form
			for (int j = 0; j < numberAttribute; j++) {
				label = inFile.next();
				outFile.print(label + " ");
			}
			outFile.println();
			// read class and convert it to numerical form
			inFile.next();

		}
		inFile.close();
		outFile.close();

	}

	// method loads training records from training file
	public void loadTrainingData(String trainingFile) throws IOException {
		Scanner inFile = new Scanner(new File(trainingFile));
		// read number of records,attributes,majority rule
		numberRecords = inFile.nextInt();
		numberAttribute = inFile.nextInt();
		numberClasses = inFile.nextInt();
		// read number of attribute values
		attributeValues = new int[numberAttribute];
		for (int i = 0; i < numberAttribute; i++)
			attributeValues[i] = inFile.nextInt();
		// list of records
		records = new ArrayList<Record>();
		// for each record
		for (int i = 0; i < numberRecords; i++) {
			// create attribute array
			int[] attributeArray = new int[numberAttribute];
			// read attributes and convert them to numerical form
			for (int j = 0; j < numberAttribute; j++) {
				String label = inFile.next();
				attributeArray[j] = convert(label, j + 1);
			}

			// read class and convert it to numerical form
			String label = inFile.next();
			int className = convert(label);
			// create record
			Record record = new Record(attributeArray, className);
			// add record to list of records
			
		}
		inFile.close();

	}

	// method computes probability values necessary for Bayes classification
	public void computeProbability() {
		// compute class probabilities
		computeClassTable();
		// compute conditional probabilities
		computeTable();

	} 

	// Method computes class probabilities
	private void computeClassTable() {
		classTable = new double[numberClasses];
		// Initialize class frequencies
		for (int i = 0; i < numberClasses; i++)
			classTable[i] = 0;
		// compute class frequencies
		for (int i = 0; i < numberRecords; i++)
			classTable[records.get(i).className - 1] += 1;
		// normalize class frequencies
		for (int i = 0; i < numberClasses; i++)
			classTable[i] /= numberRecords;

	}

	// Method computes conditional probabilities
	private void computeTable() {
		// array to store conditional probabilities
		table = new double[numberAttribute][][];
		// compute conditional probabilities of each attribute
		for (int i = 0; i < numberAttribute; i++)
			compute(i + 1);
	}

	// Method compute conditional probabilities of an attribute
	private void compute(int attribute) {
		// find number of attribute values
		int attributeValues = this.attributeValues[attribute - 1];
		// create array to hold conditional probabilities
		table[attribute - 1] = new double[numberClasses][attributeValues];
		// initialize conditional probabilities
		for (int i = 0; i < numberClasses; i++)
			for (int j = 0; j < attributeValues; j++)
				table[attribute - 1][i][j] = 0;
		// compute class -attribute frequencies
		for (int k = 0; k < numberRecords; k++) {
			int i = records.get(k).className - 1;
			int j = records.get(k).attributes[attribute - 1] - 1;
			table[attribute - 1][i][j] += 1;
		}
		// compute conditional probabilities using laplace correction
		for (int i = 0; i < numberClasses; i++)
			for (int j = 0; j < attributeValues; j++) {
				double value = (table[attribute - 1][i][j] / (classTable[i]
						* numberRecords + attributeValues));
				value = Math.round(value * 1e2) / 1e2;
				table[attribute - 1][i][j] = value;

			}
	}

	// printing class tables and laplace adjusted conditional probabilities
	public void printTables() {

		// the laplace adjusted table is printing
		System.out.println("Tha laplace adjusted table is: \n");
		for (int i = 0; i < numberAttribute; i++) {
			for (int j = 0; j < numberClasses; j++) {
				for (int k = 0; k < attributeValues[i]; k++) {
					System.out.print("table[" + i + "][" + j + "][" + k
							+ "] = " + table[i][j][k] + "\t");
				}
				System.out.println();
			}
			System.out.println();
		}
		// printing conditional probabilities

		System.out.println("The conditional probability table is:\n ");
		for (int i = 0; i < numberClasses; i++) {
			System.out.print(classTable[i] + "   ");

		}
		System.out.println();
	}

	// method classifies an attribute
	private int classify(int[] attributes) {
		double maxProbability = 0;
		int maxClass = 0;
		// for each class
		for (int i = 0; i < numberClasses; i++) {
			// find conditional probability of class given the attribute
			double probability = findProbability(i + 1, attributes);
			// choose the class with the maximum probability
			if (probability > maxProbability) {
				maxProbability = probability;
				maxClass = i;
			}
		}

		// return maximum class
		return maxClass + 1;
	}

	// method giving the level of confidence

	private double confidence(int[] attributes) {
		double maxProbability = 0;
		double confidence = 0;
		double totalProbability = 0;
		// for each class
		for (int i = 0; i < numberClasses; i++) {
			// find conditional probability of class given the attribute
			double probability = findProbability(i + 1, attributes);
			totalProbability = totalProbability + probability;
			// choose the class with the maximum probability
			if (probability > maxProbability) {
				maxProbability = probability;

			}
		}
		confidence = (maxProbability / totalProbability) * 100;
		
		// return confidence
		return confidence;

	}

	// method compute conditional probability of a class for given attributes
	private double findProbability(int className, int[] attributes) {
		double value;
		double product = 1;
		// find product of conditional probabilities stored in table
		for (int i = 0; i < numberAttribute; i++) {
			value = table[i][className - 1][attributes[i] - 1];
			product = product * value;
		}
		// multiply product and class probability
		return product * classTable[className - 1];
	}

	// method reads test records from test file and writes classes to
	// classified file
	public void classifyData(String testFile, String classifiedFile)
			throws IOException {
		Scanner inFile = new Scanner(new File(testFile));
		PrintWriter outFile = new PrintWriter(new FileWriter(classifiedFile));
		// read number of records
		int numberRecords = inFile.nextInt();

		// for each Record
		for (int i = 0; i < numberRecords; i++) {
			// create attribute array
			int[] attributeArray = new int[numberAttribute];
			// read attribute array and convert them to numerical form
			for (int j = 0; j < numberAttribute; j++) {
				String label = inFile.next();
				attributeArray[j] = convert(label, j + 1);

			}
			// find class of attribute
			int className = classify(attributeArray);
			// find class label and write to output file
			String label = convert(className);
			double confidence = confidence(attributeArray);
			outFile.println(label + " confidence is " + confidence);

		}
		inFile.close();
		outFile.close();
	}


	
	// method that compute the training error
	public void trainingError(String trainingFile, String trainingFileOut,
			String classifiedFile) throws IOException {
		int trainingError;
		int error = 0;
		trimClassifier(trainingFile, trainingFileOut);
		classifyData(trainingFileOut, classifiedFile);
		Scanner inFile1 = new Scanner(new File(classifiedFile));
		Scanner inFile2 = new Scanner(new File(trainingFile));

		numberRecords = inFile2.nextInt();
		numberAttribute = inFile2.nextInt();
		numberClasses = inFile2.nextInt();
		inFile2.nextLine();
		inFile2.nextLine();
		// for each record
		for (int i = 0; i < numberRecords; i++) {

			for (int j = 0; j < numberAttribute; j++) {
				inFile2.next();

			}

			String label2 = inFile2.next();
			String label1 = inFile1.next();

			if (label1.equals(label2)) {
				error = error + 0;
			} else {
				error = error + 1;
			}

		}

		inFile1.close();
		inFile2.close();
		trainingError = error / numberRecords * 100;
		System.out.println("the training error is " + trainingError);

	}

	// computing validation error by leave one out method
	public void validate(String trainingFile, String newTrainFile,
			String classifier, String validationFile) throws IOException {
		double validationError = 0;
		double averageValidation = 0;
		Scanner inFile = new Scanner(new File(trainingFile));
		numberRecords = inFile.nextInt();
		numberAttribute = inFile.nextInt();
		numberClasses = inFile.nextInt();

		// read neighbors,majority rule
		inFile.close();
		int i = 1;
		for (int k = 0; k <= numberRecords; k++) {
			int n;
			Scanner inFile0 = new Scanner(new File(trainingFile));
			PrintWriter outFile = new PrintWriter(new FileWriter(classifier));
			PrintWriter outFile1 = new PrintWriter(new FileWriter(newTrainFile));
			numberRecords = inFile0.nextInt();
			numberAttribute = inFile0.nextInt();
			numberClasses = inFile0.nextInt();
			outFile1.println((numberRecords - 1) + " " + numberAttribute + " "
					+ numberClasses);
			outFile.println(i);
			inFile0.nextLine();
			outFile1.println(inFile0.nextLine());
			for (n = 0; n < k; n++) {

				outFile1.println(inFile0.nextLine());
			}
			// for each record

			String label = inFile0.nextLine();
			String firstWord = label.substring(0, label.lastIndexOf(" "));
			String lastWord = label.substring((label.lastIndexOf(" ") + 1),
					label.length());
			// System.out.println("the last word is "+lastWord);
			outFile.println(firstWord);

			for (int j = n + 1; j < numberRecords; j++) {
				String label1 = inFile0.nextLine();
				outFile1.println(label1);

			}

			outFile1.close();
			outFile.close();
			inFile0.close();

			loadTrainingData(newTrainFile);
			computeProbability();
			classifyData(classifier, validationFile);
			Scanner inFile1 = new Scanner(new File(validationFile));
			String label1 = inFile1.next();
			if (label1.replaceAll("\\s+", "").equalsIgnoreCase(
					lastWord.replaceAll("\\s+", "")))
			/*	if (label1.equals(lastWord))	*/
			{
				validationError = validationError + 0;
			} else {
				validationError = validationError + 1;
			}
			inFile1.close();

		}
		double numRecord = (double) (numberRecords + 1);
		averageValidation = (validationError / numRecord) * 100;
		System.out.println("The average validation error is "
				+ averageValidation);
	}

	// method converts attribute values to numerical values.Hard coded for
	// specific application
	private int convert(String label, int column) {
		int value;
		// convert attribute labels to numerical values
		if (column == 1) {
			if (label.equals("0"))
				value = 1;
			else
				value = 2;
		}
		// convert score attribute to [0,1]range
		else if (column == 2) {
			if (label.equals("1"))
				value = 1;
			else if (label.equals("2"))
				value = 2;
			else
				value = 3;

		} else if (column == 3) {
			if (label.equals("1"))
				value = 1;
			else if (label.equals("2"))
				value = 2;
			else if (label.equals("3"))
				value = 3;
			else
				value = 4;

		}
		// convert attribute to 0/1
		else {
			if (label.equals("0"))
				value = 1;
			else
				value = 2;

		}

		return value;
	}

	// Method converts class labels to numerical values,hard coded for
	// specific application
	private int convert(String label) {
		int value;
		// convert class labels to numerical values
		if (label.equals("1"))
			value = 1;
		else if (label.equals("2"))
			value = 2;
		else if (label.equals("3"))
			value = 3;
		else
			value = 4;
		// return numerical value
		return value;
	}

	// method converts numerical values to class labels,hard copied for
	// specified application
	private String convert(int value) {
		String label;
		// convert numerical values to class labels
		if (value == 1)
			label = "1";
		else if (value == 2)
			label = "2";
		else if (value == 3)
			label = "3";
		else
			label = "4";
		return label;

	}
}
