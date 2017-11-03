package preprocess;

import postprocess.CodeSequence;

public class DriverProgram {

	public static void main(String[] args) throws Exception {
		/*String formFile = "input/code_sequence/successful.txt";
		String toFile = "input/formatted_sequence/successful.txt";
		
		SequenceData seqData = new SequenceData(formFile);
		seqData.transformToExpectedFormat(formFile, toFile);*/
		
/*		TextData.createSequences("/home/mehedi/Desktop/Obesity Data Files/FINAL Obesity files to use_fix_code_and_text",
				"HMMAndMCData/successful_fromtext.txt", "HMMAndMCData/unsuccessful_fromtext.txt", true);
		
		SmallerCodebook sc = new SmallerCodebook("HMMAndMCData/codebook_map.txt");
		sc.transform("HMMAndMCData/successful_fromtext.txt", "HMMAndMCData/successful.txt", true);
		sc.transform("HMMAndMCData/unsuccessful_fromtext.txt", "HMMAndMCData/unsuccessful.txt", true);
		sc.writeAllDistinctCode("HMMAndMCData/codebook.txt");*/
		
		CodeSequence cs = new CodeSequence();
		cs.getUniqueCodeSequenceAndWriteToFile("HMMAndMCData/successful_unsuccessful.txt", "HMMAndMCData/codebook_speaker.txt");
		
		System.out.println( "Program terminated successfully!" );
		
	}

}
