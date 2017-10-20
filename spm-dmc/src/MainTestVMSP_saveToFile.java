

import java.io.IOException;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoVMSP;


/**
 * Example of how to use the VMSP algorithm in source code.
 * @author Philippe Fournier-Viger
 */
public class MainTestVMSP_saveToFile {

	public static void main(String [] arg) throws IOException{    
		// Load a sequence database
		String input = "input/formatted_sequence/successful.txt";
		String output = "output/output_vmsp.txt";
		
		// Create an instance of the algorithm 
		AlgoVMSP algo = new AlgoVMSP(); 

		// This optional parameter allows to specify the maximum pattern length:
//		algo.setMaximumPatternLength(4);  // optional
		
		// This optional parameter allows to specify the max gap between two
		// itemsets in a pattern. If set to 1, only patterns of contiguous itemsets
		// will be found (no gap).
		//algo.setMaxGap(1);
		
        // if you set the following parameter to true, the sequence ids of the sequences where
        // each pattern appears will be shown in the result
//		algo.showSequenceIdentifiersInOutput(true);
		
		// execute the algorithm with minsup = 2 sequences  (50 %)
		algo.runAlgorithm(input, output, 0.02);    
		algo.printStatistics();
		
		System.out.println("Executed successfully");
	}
}