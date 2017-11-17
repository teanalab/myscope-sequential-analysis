package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import postprocess.CodeSequence;

public class DriverProgram {

	public static void main(String[] args) throws Exception {
		//String formFile = "input/code_sequence/successful.txt";
		//String toFile = "input/formatted_sequence/successful.txt";
		
		//SequenceData seqData = new SequenceData();
		//seqData.transformToClientFormat("/home/mehedi/Desktop/JHIR Datasets/SequenceData/37 MI/small codebook/alldata.txt",
		//		"/home/mehedi/Desktop/JHIR Datasets/SequenceData/37 MI/small codebook/alldata_CG_T.txt");
		
//		TextData.createSequencesFrom37MITranscripts("/home/mehedi/Desktop/JHIR Datasets/Textdata/Standard/teen",
//				"HMMAndMCData/successful_fromtext.txt", "HMMAndMCData/unsuccessful_fromtext.txt", true);
		
/*		SmallerCodebook sc = new SmallerCodebook("HMMAndMCData/codebook_map.txt");
		sc.transform("HMMAndMCData/successful_fromtext.txt", "HMMAndMCData/successful.txt", true);
		sc.transform("HMMAndMCData/unsuccessful_fromtext.txt", "HMMAndMCData/unsuccessful.txt", true);
		sc.writeAllDistinctCode("HMMAndMCData/codebook.txt");*/
		
		//CodeSequence cs = new CodeSequence();
		//cs.getUniqueCodeSequenceAndWriteToFile("/home/mehedi/Desktop/JHIR Datasets/SequenceData/37 MI/small codebook/alldata.txt", 
		//		"/home/mehedi/Desktop/JHIR Datasets/SequenceData/37 MI/small codebook/codebook_37_small.txt");
		//getCodebookOnly("codebook.txt", "codebook_only.txt");
		/*SmallerCodebook sc = new SmallerCodebook("/home/mehedi/Desktop/JHIR Datasets/SequenceData/37 MI/codebook for mapping.txt");
		sc.transform("/home/mehedi/Desktop/JHIR Datasets/SequenceData/37 MI/large codebook/alldata.txt",
				"/home/mehedi/Desktop/JHIR Datasets/SequenceData/37 MI/small codebook/alldata.txt", true);
		sc.writeAllDistinctCode("/home/mehedi/Desktop/JHIR Datasets/SequenceData/37 MI/small codebook/codebook_37_small.txt");*/
		
		System.out.println( "Program terminated successfully!" );
		
	}
	
	
	public static void getCodebookOnly(String inputFile, String outputFile) {
		
		Map<String, String> speakerCodeMap = new TreeMap<>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFile));			
			String line = null;
			
			while((line = br.readLine()) != null) {				
				String[] codes = line.split("\\s+");
				speakerCodeMap.put(codes[0].trim(), codes[1].trim());
			}
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			for (String c : speakerCodeMap.keySet()) {
				writer.write(c + "," + speakerCodeMap.get(c));
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
