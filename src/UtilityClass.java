import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class UtilityClass {

	public static ArrayList<CodeSequence> readSuccessfulSequence(String trainOrTest) throws Exception{
		return readSequenceFromFile("SequentialData/" + trainOrTest +"/successful.txt");
	}
	
	public static ArrayList<CodeSequence> readUnsuccessfulSequence(String trainOrTest) throws Exception{
		return readSequenceFromFile("SequentialData/" + trainOrTest +"/unsuccessful.txt");
	}
	
	public static ArrayList<CodeSequence> readAllSequence() throws Exception{
		return readSequenceFromFile("SequentialData/allsequence.txt");
	}
	
	public static ArrayList<CodeSequence> readTestSequence() throws Exception{
		return readSequenceFromFile("SequentialData/test/testdata.txt");
	}
	
	public static ArrayList<CodeSequence> readSequenceFromFile(String fileLocation) throws Exception{
		ArrayList<CodeSequence> codeSequenceList = new ArrayList<>();
		
		BufferedReader br = new BufferedReader(new FileReader(fileLocation));			
		String line = "";
		while ((line = br.readLine()) != null) {
			CodeSequence codeSequence = new CodeSequence(line);
			codeSequenceList.add(codeSequence);
		}		
		br.close();
				
		return codeSequenceList;
	}
	
	public static void createCodeSequenceFromRawData(boolean withText, HashMap<String, String> codemap) throws Exception{
		int successCount = 0, failureCount = 0;
		final String successCode = "106,112,116";
		final String failureCode = "103,109,114,115";
		//final String successCode = "106";
		//final String failureCode = "103";
		File rawDataFolder = new File("modinput");
		File []rawFiles = rawDataFolder.listFiles();
		// Sequence code writer
		PrintWriter sequenceCodeWriter = new PrintWriter("SequentialData/allsequence.txt", "UTF-8");
					
		for (int i = 0; i < rawFiles.length; ++i) {			
			BufferedReader br = new BufferedReader(new FileReader(rawFiles[i]));			
			String line = "";
			int count = 0;
			int sequenceLength = 0;
			StringBuilder sequence = new StringBuilder();
			
			while ((line = br.readLine()) != null) {
			    // process the line.
				count++;
				if(line.length() > 4){
					if(sequence.length() != 0){
						if(withText){
							sequence.append(" ==> ");
						}
						else{
							sequence.append(",");
						}
						
					}
					
					if(successCode.contains(line.substring(0, 3).trim()) && sequenceLength > 1){
						//sequence.append(line.substring(0, 3).trim()+",");
						if(withText){
							sequence.append(line.substring(0, 3).trim()+" "+codemap.get(line.substring(0, 3).trim()));
						}
						else{
							sequence.append("500");
						}
						sequenceCodeWriter.println(sequence.toString()); 
						//		+ "  \t\t " + rawFiles[i].getName() + " line " + count);
						sequence = new StringBuilder();
						sequenceLength = 0;
						successCount++;
					}			
					else if(failureCode.contains(line.substring(0, 3).trim()) && sequenceLength > 1){
						//sequence.append(line.substring(0, 3).trim()+",");
						if(withText){
							sequence.append(line.substring(0, 3).trim()+" "+codemap.get(line.substring(0, 3).trim()));
						}
						else{
							sequence.append("400");
						}
						
						sequenceCodeWriter.println(sequence.toString()); 
						//		+ "  \t\t " + rawFiles[i].getName() + " line " + count);
						sequence = new StringBuilder();
						sequenceLength = 0;
						failureCount++;
					}
					else{
						if(withText){
							sequence.append(line.substring(0, 3).trim()+" "+codemap.get(line.substring(0, 3).trim()));
						}
						else{
							sequence.append(line.substring(0, 3).trim());
						}
						sequenceLength++;
					}
				}
			
			}
			
			br.close();
		}		
		sequenceCodeWriter.close();
		System.out.println("Successful sequences: "+ successCount + ", Failure sequences: " + failureCount);
	}
	
	public static void buildMap(HashMap<String,String> codemap) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader("myscopecode.txt"));			
		String line = "";		
		while ((line = br.readLine()) != null) {
			codemap.put(line.substring(0, 3).trim(),line.substring(4).trim());
		}
		br.close();
	}
	
	public static void printSequenceByMapping(String seqCode, HashMap<String, String> codemap){
		String codes[] = seqCode.split(",");
		for(int i=0; i<codes.length; i++){
			if(i == codes.length-1)
				System.out.print(codes[i].trim());
			else
				System.out.print(codes[i].trim()+ " " + codemap.get(codes[i].trim())+" ==> ");
		}
		System.out.println("\n");
	}
	
	public static void printCodeMap(HashMap<String, String> codemap) throws Exception{
		String files[] = {"caregiver-code-16.txt","caregiver-code-19.txt","caregiver-code-58.txt","adolescent-code-17.txt","adolescent-code-21.txt","adolescent-code-41.txt"};
		
		for(int i=0; i<files.length; i++){
			BufferedReader br;
			PrintWriter writer = new PrintWriter("map/mapping_"+files[i], "UTF-8");
			try {
				br = new BufferedReader(new FileReader("map/"+files[i]));
				String line = "";		
				while ((line = br.readLine()) != null) {
					writer.println(line.substring(0, 3).trim() + " " + codemap.get(line.substring(0, 3).trim())+" ==> " + line.substring(4).trim() + " " + codemap.get(line.substring(4).trim()));
				}
				br.close();
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
}
