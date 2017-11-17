import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPClose;
import postprocess.MyScopeCodeSequence;
import preprocess.SequenceData;

/**
 * Example of how to use FPClose from the source code and
 * the result to a file.
 * @author Philippe Fournier-Viger (Copyright 2015)
 */
public class MainTestFPClose_saveToFile {

	public static void main(String [] arg) throws FileNotFoundException, IOException{
		/*// the file paths
		String input = fileToPath("contextPasquier99.txt");  // the database
		String output = ".//output.txt";  // the path for saving the frequent itemsets found
		
		double minsup = 0.4; // means a minsup of 2 transaction (we used a relative support)

		// Applying the algorithm
		AlgoFPClose algo = new AlgoFPClose();
		algo.runAlgorithm(input, output, minsup);
		algo.printStats();*/
		
		// the file paths
		// preprocess data
		String dataType = "unsuccessful";
		String codeFile = "input/code_sequence/" + dataType + ".txt";
		String formatted_input = "input/formatted_sequence/" + dataType + ".txt";
		
		SequenceData seqData = new SequenceData(codeFile);
		seqData.transformToFrequentItemsetFormat(codeFile, formatted_input, true);

		// Load a sequence database
		String formattedoutput = "output/formatted_patterns/" + dataType + "_fis.txt";
		String codeoutput = "output/code_patterns/" + dataType + "_fis.txt";
		
		double minsup = 0.02; // means a minsup of 2 transaction (we used a relative support)

		// Applying the algorithm
		AlgoFPClose algo = new AlgoFPClose();
		algo.runAlgorithm(formatted_input, formattedoutput, minsup);
		algo.printStats();
		
		MyScopeCodeSequence mcs = new MyScopeCodeSequence(formattedoutput, seqData.intToCodeMap);
		mcs.printToFile(codeoutput);
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestFPClose_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
