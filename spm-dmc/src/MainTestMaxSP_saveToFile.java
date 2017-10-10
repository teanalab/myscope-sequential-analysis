

import java.io.IOException;

import ca.pfv.spmf.algorithms.sequentialpatterns.BIDE_and_prefixspan.AlgoMaxSP;
import ca.pfv.spmf.input.sequence_database_list_integers.SequenceDatabase;
/*
 * Example of how to use the BIDE+ algorithm, from the source code.
 */
public class MainTestMaxSP_saveToFile {

	public static void main(String [] arg) throws IOException{    
		// Load a sequence database
		SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
		sequenceDatabase.loadFile("input/contextPrefixSpan.txt");
//		sequenceDatabase.print();
		
		int minsup = 2; // we use a minsup of 2 sequences (50 % of the database size)
		
		AlgoMaxSP algo  = new AlgoMaxSP();  //
		
        // if you set the following parameter to true, the sequence ids of the sequences where
        // each pattern appears will be shown in the result
        algo.setShowSequenceIdentifiers(false);
		
		// execute the algorithm
		algo.runAlgorithm(sequenceDatabase, "output/output_maxsp.txt", minsup);    
		algo.printStatistics(sequenceDatabase.size());
	}
}