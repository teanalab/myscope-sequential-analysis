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
	
	public static ArrayList<String> createAllSequencesFromOne(String seq){
		
		String seqArr[] = seq.split(",");
		ArrayList<ArrayList<String>> listOflistArray = new ArrayList<ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		int prev = Integer.parseInt(seqArr[0]);
		
		for(int i=0; i < seqArr.length; i++){
			int curCode = Integer.parseInt(seqArr[i].trim());
			if((prev < 300 && curCode < 300) || (prev > 300 && prev < 399 && curCode > 300 && curCode < 399) || (prev >= 400 && curCode >= 400)){
				list.add(seqArr[i].trim());
			}
			else{
				listOflistArray.add(list);
				list = new ArrayList<String>();
				list.add(seqArr[i].trim());
				prev = Integer.parseInt(seqArr[i]);
			}
			
			if(i == seqArr.length-1 && list.size() > 0){
				listOflistArray.add(list);
			}
		}
		
		// get combination of sequences
		while(listOflistArray.size() >= 2){
			
			ArrayList<String> listL2 = listOflistArray.get(listOflistArray.size()-1);
			ArrayList<String> listL1 = listOflistArray.get(listOflistArray.size()-2);
			ArrayList<String> newList = new ArrayList<String>();
			String s = "";
			
			for(int i=0; i<listL1.size(); i++){
				s = listL1.get(i); 
				for(int j=0; j<listL2.size(); j++){
					newList.add(s + "," + listL2.get(j));
				}
			}
			
			listOflistArray.remove(listOflistArray.size()-1);
			listOflistArray.remove(listOflistArray.size()-1);
			listOflistArray.add(newList);
		}
		
		return listOflistArray.get(0);
	}
	
	
	public static int writeSequenceIntoFile(PrintWriter sequenceCodeWriter, String seqLine){
		ArrayList<String> list = createAllSequencesFromOne(seqLine);
		for(int i=0; i<list.size(); i++){
			sequenceCodeWriter.println(list.get(i).trim());
		}
		return list.size();
	}
	
	public static void writePairwiseFrequencyIntoFile(int pair[][], String seqLine, PrintWriter pairWriter){
		String seqArr[] = seqLine.split(",");
		ArrayList<ArrayList<String>> listOflistArray = new ArrayList<ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		int prev = Integer.parseInt(seqArr[0]);
		
		for(int i=0; i < seqArr.length; i++){
			int curCode = Integer.parseInt(seqArr[i].trim());
			if((prev < 300 && curCode < 300) || (prev > 300 && prev < 399 && curCode > 300 && curCode < 399)){
				list.add(seqArr[i].trim());
			}
			else{
				listOflistArray.add(list);
				list = new ArrayList<String>();
				list.add(seqArr[i].trim());
				prev = Integer.parseInt(seqArr[i]);
			}
			
			if(i == seqArr.length-1 && list.size() > 0){
				listOflistArray.add(list);
			}
		}
		
		for(int k=0; k < listOflistArray.size()-1; k++){
				
			ArrayList<String> listL1 = listOflistArray.get(k);
			ArrayList<String> listL2 = listOflistArray.get(k+1);
			
			for(int i=0; i < listL1.size(); i++){ 
				int idx = Integer.parseInt(listL1.get(i));
				for(int j=0; j < listL2.size(); j++){
					int idy = Integer.parseInt(listL2.get(j));
					pair[idx][idy] = pair[idx][idy]+1;
				}
			}
		}
	}
	
	public static void calculateFreqOfSequences(String fileLocation, String dest){
		try {
			HashMap<String, Integer> map = new HashMap<>();
			BufferedReader br = new BufferedReader(new FileReader(fileLocation));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(map.containsKey(line.trim())){
					map.put(line.trim(), map.get(line.trim())+1);
				}
				else{
					map.put(line.trim(), 1);
				}
			}		
			br.close();
			
			PrintWriter pWriter = new PrintWriter(dest, "UTF-8");
			pWriter.println("Sequence, Frequency");
			
			for(String key: map.keySet()){
				pWriter.println(key.replace(",", "==>") + "," + map.get(key));
			}
			
			pWriter.close();	
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	public static void createCodeSequenceFromRawData(boolean withText, HashMap<String, String> codemap, boolean withAlternateSeq) throws Exception{
		int successCount = 0, failureCount = 0;
		final String successCode = "106,112,116";
		final String failureCode = "103,109,114,115";
		//final String successCode = "106";
		//final String failureCode = "103";
		File rawDataFolder = new File("modinput/collapse");
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
						
						if(withAlternateSeq){
							successCount = successCount + writeSequenceIntoFile(sequenceCodeWriter, sequence.toString());
						}
						else{
							sequenceCodeWriter.println(sequence.toString()); 
							successCount++;
						}
						//		+ "  \t\t " + rawFiles[i].getName() + " line " + count);
						sequence = new StringBuilder();
						sequenceLength = 0;						
					}			
					else if(failureCode.contains(line.substring(0, 3).trim()) && sequenceLength > 1){
						//sequence.append(line.substring(0, 3).trim()+",");
						if(withText){
							sequence.append(line.substring(0, 3).trim()+" "+codemap.get(line.substring(0, 3).trim()));
						}
						else{
							sequence.append("400");
						}
						
						if(withAlternateSeq){
							failureCount = failureCount + writeSequenceIntoFile(sequenceCodeWriter, sequence.toString());
						}
						else{
							sequenceCodeWriter.println(sequence.toString()); 
							failureCount++;
						}
						//		+ "  \t\t " + rawFiles[i].getName() + " line " + count);
						sequence = new StringBuilder();
						sequenceLength = 0;
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
	
	public static void createCombinationOfCodeSequence(boolean isNormal) throws Exception{
		
		int successCount = 0, failureCount = 0;
		final String successCode = "106,112,116";
		final String failureCode = "103,109,114,115";
		File rawDataFolder = new File("modinput/collapse");
		File []rawFiles = rawDataFolder.listFiles();
		PrintWriter sequenceCodeWriter = new PrintWriter("SequentialData/allsequence.txt", "UTF-8");
					
		for (int i = 0; i < rawFiles.length; ++i) {			
			BufferedReader br = new BufferedReader(new FileReader(rawFiles[i]));			
			String line = "";
			int sequenceLength = 0;
			StringBuilder sequence = new StringBuilder();
			
			while ((line = br.readLine()) != null) {
				if(line.length() > 4){
					if(sequence.length() != 0){
						sequence.append(",");					
					}
					
					if(successCode.contains(line.substring(0, 3).trim()) && sequenceLength > 1){
						sequence.append(line.substring(0, 3).trim());
						if(isNormal){
							sequenceCodeWriter.println(sequence.toString()); 
							successCount++;
						}
						else{
							successCount = successCount + writeSequenceIntoFile(sequenceCodeWriter, sequence.toString());
						}
						sequence = new StringBuilder();
						sequenceLength = 0;						
					}			
					else if(failureCode.contains(line.substring(0, 3).trim()) && sequenceLength > 1){
						sequence.append(line.substring(0, 3).trim());
						if(isNormal){
							sequenceCodeWriter.println(sequence.toString()); 
							failureCount++;
						}
						else{
							failureCount = failureCount + writeSequenceIntoFile(sequenceCodeWriter, sequence.toString());
						}						
						sequence = new StringBuilder();
						sequenceLength = 0;
					}
					else{
						sequence.append(line.substring(0, 3).trim());
						sequenceLength++;
					}
				}
			
			}
			
			br.close();
		}		
		sequenceCodeWriter.close();
		System.out.println("Successful sequences: "+ successCount + ", Failure sequences: " + failureCount);
	}
	
public static void calculateDistributionOfPairSequence(String dest) throws Exception{
	
		int pair[][] = new int[400][400];
		PrintWriter pairWriter = new PrintWriter(dest, "UTF-8");						
		String line = "";		
		
		File rawDataFolder = new File("modinput/rawdata");
		File []rawFiles = rawDataFolder.listFiles();
					
		for (int i = 0; i < rawFiles.length; ++i) {			
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(rawFiles[i]));			
			StringBuilder sequence = new StringBuilder();
			
			while ((line = br.readLine()) != null) {
				if(line.length() > 4){
					if(sequence.length() != 0){
						sequence.append(",");					
					}
					sequence.append(line.substring(0, 3).trim());
				}
			}
			
			writePairwiseFrequencyIntoFile(pair, sequence.toString(), pairWriter);
		}
		
		// define selected columns and rows for generating the file
		int cols = 20;
		int rows = 60;
		for(int j=1; j <= cols; j++)
			pairWriter.print("," + "Y" + (100+j) );
		
		pairWriter.println();
		for(int i = 1; i <= rows; i++){
			int rowsum = 0;
			for(int j=1; j <= cols; j++) {
                rowsum += pair[i + 300][j + 100];
            }

			pairWriter.print("C"+(300+i));
			for(int j=1; j <= cols; j++){
				if(rowsum != 0)
					pairWriter.print("," + ((double)pair[i+300][j+100]/rowsum));
				else
					pairWriter.print("," + pair[i+300][j+100]);
			}
			pairWriter.println();
		}
			
		pairWriter.close();
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
				e.printStackTrace();
			}	
		}
	}
	
	public static void preprocessMITranscripts(String inputLocation, String outLocation) throws Exception{
		
		File rawDataFolder = new File(inputLocation);
		File []rawFiles = rawDataFolder.listFiles();
					
		for (int i = 0; i < rawFiles.length; ++i) {	
			if(rawFiles[i].getName().contains(".txt")){
				
				HashMap<String, DataDetail> mapTimestamp = new HashMap<>();
				PrintWriter writer = new PrintWriter(outLocation+rawFiles[i].getName());
				BufferedReader br = new BufferedReader(new FileReader(inputLocation+"/"+rawFiles[i].getName().replace(".txt", ".csv")));			
				String line = "";				
				br.readLine();
				
				while ((line = br.readLine()) != null) {
					if(!line.isEmpty()){
						String record[] = line.split(",");
						String timestamp = getTimestamp((record[2].substring(1, 8).replace(":", "")+record[3].trim()).trim());
						if(mapTimestamp.containsKey(timestamp)){
							mapTimestamp.get(timestamp).listTimestamp.add(record[4].trim());
						}
						else{
							DataDetail entry = new DataDetail();
							if(record.length < 5)
								continue;
							entry.code = record[4].trim();
							entry.who = record[3].trim();
							entry.timestamp = timestamp;
							entry.listTimestamp.add(entry.code);
							
							mapTimestamp.put(timestamp, entry);
						}
					}
				}				
				
				br.close();
				
				br = new BufferedReader(new FileReader(rawFiles[i]));
				
				while ((line = br.readLine()) != null) {
					if(!line.isEmpty() && line.length() > 7){
						line = line.replace("’", "'").replace("—", "-").replace("‘", "'").replace("…", "�").replace("PPT:", "PT:");
						int index = line.substring(1, 13).lastIndexOf(":");
						if(index < 0){
							continue;
						}
						String timestamp = line.substring(1, index+1).replace(":", "").replace(")", "").trim();
						if(mapTimestamp.containsKey(timestamp.trim())){
							String actualTimestamp = "("+timestamp.substring(0, 3)+":"+timestamp.substring(3, 5)+")";
							writer.println(actualTimestamp + ",\t["+mapTimestamp.get(timestamp).code + "],\t" + line.substring(8).trim().replace(",", " "));
							for(int j = 1; j < mapTimestamp.get(timestamp).listTimestamp.size(); j++){
								writer.println(actualTimestamp + ",\t["+mapTimestamp.get(timestamp).listTimestamp.get(j) + "],\t" + timestamp.substring(5) + ":\tMISSING TEXT");
							}
						}
						else{
							//System.out.println(timestamp);
						}
						writer.flush();
					}
				}				
				
				br.close();
				
				writer.close();
			}
		}		
	}
	
	public static String getTimestamp(String time){
		int t = Integer.parseInt(time.substring(0, 1))*60+Integer.parseInt(time.substring(1, 3));
		String timestamp = t + time.substring(3);
		timestamp = t<10?"00"+timestamp:timestamp;
		timestamp = (t>9&&t<100)?"0"+timestamp:timestamp;
		return timestamp;
	}
}
