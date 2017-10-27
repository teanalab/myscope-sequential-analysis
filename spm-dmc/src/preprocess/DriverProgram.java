package preprocess;

public class DriverProgram {

	public static void main(String[] args) {
		/*String formFile = "input/code_sequence/successful.txt";
		String toFile = "input/formatted_sequence/successful.txt";
		
		SequenceData seqData = new SequenceData(formFile);
		seqData.transformToExpectedFormat(formFile, toFile);*/
		
		SmallerCodebook sc = new SmallerCodebook("HMMAndMCData/codebook_map.txt");
		sc.transform("HMMAndMCData/successful_raw.txt", "HMMAndMCData/successful.txt");
		sc.transform("HMMAndMCData/unsuccessful_raw.txt", "HMMAndMCData/unsuccessful.txt");
		sc.writeAllDistinctCode("HMMAndMCData/codebook.txt");
	}

}
