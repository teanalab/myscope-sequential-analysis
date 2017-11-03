import java.io.IOException;
import java.util.PriorityQueue;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoTKS;
import ca.pfv.spmf.algorithms.sequentialpatterns.spam.PatternTKS;
import postprocess.MyScopeCodeSequence;
import preprocess.SequenceData;


/**
 * Example of how to use the TKS algorithm in source code.
 * @author Philippe Fournier-Viger
 */
public class MainTestTKS {

	public static void main(String [] arg) throws IOException{ 
		
		// preprocess data
		String dataType = "unsuccessful";
		String codeFile = "input/code_sequence/" + dataType + ".txt";
		String formatted_input = "input/formatted_sequence/" + dataType + ".txt";
		
		SequenceData seqData = new SequenceData(codeFile);
		seqData.transformToExpectedFormat(codeFile, formatted_input);

		// Load a sequence database
		String formattedoutput = "output/formatted_patterns/" + dataType + "_tks.txt";
		String codeoutput = "output/code_patterns/" + dataType + "_tks.txt";
		
		int k = 1000;
		
		// Create an instance of the algorithm 
		AlgoTKS algo = new AlgoTKS(); 
		
		// This optional parameter allows to specify the minimum pattern length:
		algo.setMinimumPatternLength(3);  // optional

		// This optional parameter allows to specify the maximum pattern length:
//		algo.setMaximumPatternLength(4);  // optional
		
		// This optional parameter allows to specify constraints that some
		// items MUST appear in the patterns found by TKS
		// E.g.: This requires that items 1 and 3 appears in every patterns found
//		algo.setMustAppearItems(new int[] {1,3});
		
		// This optional parameter allows to specify the max gap between two
		// itemsets in a pattern. If set to 1, only patterns of contiguous itemsets
		// will be found (no gap).
//		algo.setMaxGap(1);
		
	    // if you set the following parameter to true, the sequence ids of the sequences where
        // each pattern appears will be shown in the result
//		algo.showSequenceIdentifiersInOutput(true);
		
		// execute the algorithm, which returns some patterns
		PriorityQueue<PatternTKS> patterns = algo.runAlgorithm(formatted_input, formattedoutput, k);  
		// save results to file
		algo.writeResultTofile(formattedoutput);   
		algo.printStatistics();
		
		MyScopeCodeSequence mcs = new MyScopeCodeSequence(formattedoutput, seqData.intToCodeMap);
		mcs.printToFile(codeoutput);

	}
}