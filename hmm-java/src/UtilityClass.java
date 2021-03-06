import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			if(list.get(i).trim().length() > 9)
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
	
	public static void createCodeSequenceFromRawData(boolean withText, HashMap<String, String> codemap, boolean withAlternateSeq, int order) throws Exception{
		int successCount = 0, failureCount = 0;
		final String successCode = "106,112,116";
		final String failureCode = "103,109,114,115";
		File rawDataFolder = new File("modinput/collapse");
		File []rawFiles = rawDataFolder.listFiles();
		// Sequence code writer
		PrintWriter sequenceCodeWriter = new PrintWriter("SequentialData/allsequence_noorder.txt", "UTF-8");

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
						
						// handle nth order
						String seqStr = "";
						String []lineData = sequence.toString().split(",");
						if(lineData.length < 2)
							continue;
						
						for(int p=0; p<lineData.length-2; p++){
							seqStr = seqStr.length()>0?seqStr+","+lineData[p]+":"+lineData[p+1]:lineData[p]+":"+lineData[p+1];
						}
						
						//System.out.println(seqStr);
						
						if(withAlternateSeq){
							successCount = successCount + writeSequenceIntoFile(sequenceCodeWriter, sequence.toString());
						}
						else{
							sequenceCodeWriter.println(seqStr+",500"); 
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
						
						// handle nth order
						String seqStr = "";
						String []lineData = sequence.toString().split(",");
						if(lineData.length < 2)
							continue;
						
						for(int p=0; p<lineData.length-2; p++){
							seqStr = seqStr.length()>0?seqStr+","+lineData[p]+":"+lineData[p+1]:lineData[p]+":"+lineData[p+1];
						}
						
						//System.out.println(seqStr);
						
						if(withAlternateSeq){
							failureCount = failureCount + writeSequenceIntoFile(sequenceCodeWriter, sequence.toString());
						}
						else{
							sequenceCodeWriter.println(seqStr+",400"); 
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
	
	public static void createCombinationOfCodeSequence(boolean isCumulative) throws Exception{
		
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
						if(!isCumulative){
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
						if(!isCumulative){
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
					if(!line.isEmpty() && line.length() > 8){
						String record[] = line.split(",");
						if(record.length < 5 || !line.contains(":"))
							continue;
						//System.out.println(line);
						String timestamp = "";
						try{
							timestamp = getTimestamp((record[2].substring(1, 8).replace(":", "")+record[3].trim()).trim());
							
							//System.out.println(timestamp);
						}
						catch(Exception e){
							System.err.println(rawFiles[i].getName() + " text: " + line);
							timestamp = record[2].replace(":", "")+record[3].trim();
						}
						
						timestamp = timestamp.replaceAll("\\u00a0","");
						
						//System.out.println(rawFiles[i].getName() +timestamp);
						if(mapTimestamp.containsKey(timestamp)){
							mapTimestamp.get(timestamp).listTimestamp.add(record[4].trim());
						}
						else{
							DataDetail entry = new DataDetail();
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
				// annotation Found
				int annotationFound = 0;
				
				while ((line = br.readLine()) != null) {
					if(!line.isEmpty() && line.length() > 7){
						
						line = line.replace("\t", " ");
						
						if(!line.contains(":"))
							continue;
						line = line.replace("’", "'").replace("—", "-").replace("‘", "'").replace("…", "�").replace("PPT:", "PT:");
						
						String timestamp = "";
						TimestampAndIndex tsAndIdx = new TimestampAndIndex();							
						getTimestampFromText(line, tsAndIdx);	
						
						//System.out.println(line + " : " + tsAndIdx.timestamp);
												
						if(tsAndIdx.timestamp == null || tsAndIdx.timestamp.length() < 1)
							continue;
						else
							timestamp = tsAndIdx.timestamp+tsAndIdx.who;
						
						if(tsAndIdx.timestamp.length() == 3)
							timestamp = "00" + timestamp;
						else if(tsAndIdx.timestamp.length() == 4)
							timestamp = "0" + timestamp;
						
						if(mapTimestamp.containsKey(timestamp.trim().substring(0, timestamp.trim().length()-1)+"PT"))
							timestamp = timestamp.trim().substring(0, timestamp.trim().length()-1)+"PT";
						else if(mapTimestamp.containsKey(timestamp.trim().substring(0, timestamp.trim().length()-1)+"Y"))
							timestamp = timestamp.trim().substring(0, timestamp.trim().length()-1)+"Y";
						else if(mapTimestamp.containsKey(timestamp.trim().substring(0, timestamp.trim().length()-1)+"CG"))
							timestamp = timestamp.trim().substring(0, timestamp.trim().length()-1)+"CG";
						else if(mapTimestamp.containsKey(timestamp.trim().substring(0, timestamp.trim().length()-1)+"CHW"))
							timestamp = timestamp.trim().substring(0, timestamp.trim().length()-1)+"CHW";
						
						//System.out.println(timestamp);
						//System.out.println(mapTimestamp);
						
						timestamp  = timestamp.replaceAll(" ", "");
						if(mapTimestamp.containsKey(timestamp.trim())){
							String actualTimestamp = "("+timestamp.substring(0, 3)+":"+timestamp.substring(3, 5)+")";
							String whom = mapTimestamp.get(timestamp).who;
							if(!whom.contains(":"))
								whom = whom + ": ";
								
							writer.println(actualTimestamp + ",\t["+mapTimestamp.get(timestamp).code + "],\t" + whom + line.substring(tsAndIdx.idx).trim().replace(",", " "));
							for(int j = 1; j < mapTimestamp.get(timestamp).listTimestamp.size(); j++){
								writer.println(actualTimestamp + ",\t["+mapTimestamp.get(timestamp).listTimestamp.get(j) + "],\t" + timestamp.substring(5) + ":\tMISSING TEXT");
								annotationFound++;
							}
							annotationFound++;
							//System.out.println(line + ": " + rawFiles[i].getName() + "\t" + timestamp);
						}
						else{
							//System.out.println(rawFiles[i].getName() + "\t" + timestamp + "[stat] :" + annotationFound + " : " + mapTimestamp.size());
							//System.out.println(line + ": " + rawFiles[i].getName() + "\t" + timestamp);
							//System.out.println();
							//if(rawFiles[i].getName().contains("F354"))								
							//System.out.println("Missing: " + timestamp);
						}
					}
				}				
				
				if(annotationFound < mapTimestamp.size())
					System.out.println(rawFiles[i].getName() + "\t" + "[stat] :" + annotationFound + " : " + mapTimestamp.size());
				
				//System.out.println(mapTimestamp + "\n\n");
				br.close();
				writer.flush();
				writer.close();
			}
		}		
	}
	
	public static String getTimestamp(String time){
		try{
			int t = Integer.parseInt(time.substring(0, 1))*60+Integer.parseInt(time.substring(1, 3));
			String timestamp = t + time.substring(3);
			timestamp = t<10?"00"+timestamp:timestamp;
			timestamp = (t>9&&t<100)?"0"+timestamp:timestamp;						
			return timestamp;
		}
		catch(Exception e){
			System.err.println("time: " + time);
			return "";
		}
		
	}
	
	public static void getTimestampFromText(String line, TimestampAndIndex ts){
		
		String timestamp = "";
		
		// Check for valid text
		int maxLen = line.length() < 17?line.length():17;
		int index = line.substring(1, maxLen).lastIndexOf(":");
		if(index < 0) return;
		
		// Create patterns
		String lineTimestamp = line.substring(0, maxLen);
		
		Pattern regexp1 =  Pattern.compile("(([\\(\\{\\[]([0-9]+:[0-9]+)[\\)\\}\\]])([0-9a-zA-Z]+)(:)*)");
		Matcher matcher1 = regexp1.matcher(lineTimestamp);
		
		Pattern regexp2 =  Pattern.compile("(([\\(\\{\\[]([0-9]+:[0-9]+\\.[0-9])[\\)\\}\\]])([0-9a-zA-Z]+)(:)*)");
		Matcher matcher2 = regexp2.matcher(lineTimestamp);
		
		Pattern regexp3 =  Pattern.compile("(([0-9]+:[0-9]+:[0-9]+)(\\s+)([0-9a-zA-Z]+)(:)*)");
		Matcher matcher3 = regexp3.matcher(lineTimestamp);
		
		Pattern regexp4 =  Pattern.compile("(([0-9]+:[0-9]+:[0-9]+\\.[0-9])(\\s+)([0-9a-zA-Z]+)(:)*)");
		Matcher matcher4 = regexp4.matcher(lineTimestamp);
		
		Pattern regexp5 =  Pattern.compile("(([\\[\\{\\(][0-9]+:[0-9]+:[0-9]+[\\]\\}\\)])(\\s+)([0-9a-zA-Z]+)(:)*)");
		Matcher matcher5 = regexp5.matcher(lineTimestamp);
		
		Pattern regexp6 =  Pattern.compile("(([\\[\\{\\(][0-9]+:[0-9]+:[0-9]+\\.[0-9][\\]\\}\\)])(\\s+)([0-9a-zA-Z]+)(:)*)");
		Matcher matcher6 = regexp6.matcher(lineTimestamp);
		
		Pattern regexp7 =  Pattern.compile("(([0-9]+:\\s[0-9]+:[0-9]+\\.[0-9])(\\s+)([0-9a-zA-Z]+)(:)*)");
		Matcher matcher7 = regexp7.matcher(lineTimestamp);
		
		Pattern regexp8 =  Pattern.compile("([a-zA-Z]+(\\s+)([0-9]+:[0-9]+:[0-9]+))");
		Matcher matcher8 = regexp8.matcher(lineTimestamp);
		
		Pattern regexp9 =  Pattern.compile("(([\\(\\{\\[]([0-9]+:[0-9]+)[\\)\\}\\]])(\\s[0-9a-zA-Z]+)(:)*)");
		Matcher matcher9 = regexp9.matcher(lineTimestamp);
		
		// Check for several patterns
		String s = "", speaker = "";
		if(matcher1.find()){
			s = matcher1.group();
			timestamp = matcher1.group(2).replace("(", "").replace("{", "").replace("[", "").replace(")", "").replace("}", "").replace("]", "").replaceAll(":", "");
			speaker = matcher1.group(4);
			//System.out.println(lineTimestamp + " : " + s + " part: " + speaker);
			ts.idx = s.length();
			ts.who = speaker;
			ts.timestamp = timestamp;
			return;
		}
		else if(matcher2.find()){
			s = matcher2.group();
			timestamp = matcher2.group(2).replace("(", "").replace("{", "").replace("[", "").replace(")", "").replace("}", "").replace("]", "");
			timestamp = timestamp.substring(0, timestamp.length()-2);
			speaker = matcher2.group(4);
			//System.out.println(lineTimestamp + " : " + s + " part: " + speaker);
			ts.idx = s.length();
			ts.who = speaker;
			ts.timestamp = timestamp;
			return;
		}
		else if(matcher8.find()){
			s = matcher8.group().trim();
			timestamp = s.split("\\s+")[1].substring(1).replaceAll(":", "");
			speaker = s.split("\\s+")[0].trim();
			//System.out.println(timestamp + " " + lineTimestamp + " : " + s + " part: " + speaker);
			//System.out.println(matcher8.group(0) + ":::" + matcher8.group(1) + matcher8.group(2));
		}
		else if(matcher3.find()){
			s = matcher3.group();
			timestamp = matcher3.group(2).substring(0).replaceAll(":", "");
			if(timestamp.length() == 6)
				timestamp = timestamp.substring(1);
			speaker = matcher3.group(4);
			//System.out.println(timestamp + " " + lineTimestamp + " : " + s + " part: " + speaker);
		}
		else if(matcher4.find()){
			s = matcher4.group();
			timestamp = matcher4.group(2).substring(0, matcher4.group(2).length()-2).substring(1).replaceAll(":", "");
			speaker = matcher4.group(4);
			//System.out.println(lineTimestamp + " : " + s + " part: " + speaker);
		}
		else if(matcher5.find()){
			s = matcher5.group();
			timestamp = matcher5.group(2).replace("(", "").replace("{", "").replace("[", "").replace(")", "").replace("}", "").replace("]", "").substring(1).replaceAll(":", "");
			speaker = matcher5.group(4);
			//System.out.println(lineTimestamp + " : " + s + " part: " + speaker);
		}
		else if(matcher6.find()){
			s = matcher6.group();
			timestamp = matcher6.group(2).replace("(", "").replace("{", "").replace("[", "").replace(")", "").replace("}", "").replace("]", "");
			timestamp = timestamp.substring(0, timestamp.length()-2).substring(1).replaceAll(":", "");
			speaker = matcher6.group(4);
			System.out.println(lineTimestamp + " : " + s + " part: " + speaker);
		}
		else if(matcher7.find()){
			s = matcher7.group();
			timestamp = matcher7.group(2).substring(0, matcher7.group(2).length()-2).substring(1).replaceAll(":", "").replaceAll(" ", "");
			speaker = matcher7.group(4);
			//System.out.println(lineTimestamp + " : " + s + " part: " + speaker);
		}
		else if(matcher9.find()){
			s = matcher9.group();
			timestamp = matcher9.group(2).replace("(", "").replace("{", "").replace("[", "").replace(")", "").replace("}", "").replace("]", "").replaceAll(":", "").replaceAll(" ", "");
			speaker = matcher9.group(4);
			//System.out.println(lineTimestamp + " : " + s + " part: " + speaker);
			ts.idx = s.length();
			ts.who = speaker;
			ts.timestamp = timestamp;
			return;
		}
		else{
			//System.out.println(lineTimestamp + " : ");
			return;
		}
		
		ts.idx = s.length();
		ts.who = speaker;
		ts.timestamp = getTimestamp(timestamp);
	}
	
	public static Map<String, String> readMergeCode(String fileName) throws IOException {
		  
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    Map<String, String> mergeCodeMap = new HashMap<String, String>();  
	    
	    try {
	    	
	        String[] oneLine;
	        String line = br.readLine();

	        while (line != null) {
	        	oneLine = line.split("\t");
	        	mergeCodeMap.put(oneLine[0], oneLine[1]);
	            line = br.readLine();	            
	        }
	        
	        br.close();
	        return mergeCodeMap;
	        
	    } catch(Exception e) {
	        br.close();
	        return mergeCodeMap;
	    }
	}
	
	public static void createNthOrderSequence(int order) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("SequentialData/allsequence_noorder.txt"));
	    PrintWriter writer = new PrintWriter(new File("SequentialData/allsequence.txt"));
	    try {
	    	
	        String line = br.readLine();

	        while (line != null) {
	        	// handle nth order
				String seqStr = "";
				String []lineData = line.trim().split(",");
				if(lineData.length < 3){
					line = br.readLine();
					continue;
				}
				
				for(int p=0; p<lineData.length-2; p++){
					seqStr = seqStr.length()>0?seqStr+","+lineData[p]+":"+lineData[p+1]:lineData[p]+":"+lineData[p+1];
				}
				
				writer.println(seqStr+","+lineData[lineData.length-1].trim());
	        	
	        	line = br.readLine();	            
	        }
	        
	        br.close();
	        writer.close();
	        
	    } catch(Exception e) {
	        br.close();
	        writer.close();
	    }
	}
	
	public static void splitSentenceForMSDoc(String inputFolder, String outputFolder) throws IOException {
	    
	    File rawDataFolder = new File(inputFolder);
		File []files = rawDataFolder.listFiles();
		for (int i = 0; i < files.length; i++) {	
	    
			BufferedReader br = new BufferedReader(new FileReader(files[i]));
		    BufferedWriter bw = new BufferedWriter(new FileWriter(outputFolder + "/" + files[i].getName()));
		    
		    try {
		        br.readLine();
		        String line = br.readLine().trim();
		        int lineSize = 105;
		        
		        while (line != null) {
		        	if(line.length() > lineSize){
		        		while(line.length() > lineSize){
		        			int spaceIndex = lineSize;
		        			while(line.charAt(spaceIndex) != ' ')
		        				spaceIndex--;
		        			String thisLine = line.substring(0, spaceIndex).trim();
		        			line = line.substring(spaceIndex).trim();
		        			bw.write(thisLine + "\n");
		        		}
		        		
		        		if(line.length() > 0)
		        			bw.write(line + "\n");
		        	}
		        	else
		        		bw.write(line + "\n");
		            line = br.readLine().trim();	            
		        }
		        
		        br.close();
		        bw.close();
		        
		    } catch(Exception e) {
		        br.close();
		        bw.close();
		    }
		}
	}
	
	public static void preprocesseCoachingData(String inputFolder, String outputFolder) throws IOException {
	    
	    File rawDataFolder = new File(inputFolder);
		File []files = rawDataFolder.listFiles();
		for (int i = 0; i < files.length; i++) {
	    
			if(files[i].getName().contains(".txt")){				
				
				Map<String, String> lineMap = getLineMapping(inputFolder+ "/" + files[i].getName().replace(".txt", ".csv"));
				
				BufferedReader br = new BufferedReader(new FileReader(files[i]));
			    BufferedWriter bw = new BufferedWriter(new FileWriter(outputFolder + "/" + files[i].getName()));
			    
			    try {
			        br.readLine();
			        String line = br.readLine().trim();
			        int lineSize = 115;
			        int lineCount = 0;
			        
			        while (line != null) {
			        	if(line.length() > lineSize){
			        		while(line.length() > lineSize){
			        			int spaceIndex = lineSize;
			        			while(line.charAt(spaceIndex) != ' ')
			        				spaceIndex--;
			        			String thisLine = line.substring(0, spaceIndex).trim();
			        			line = line.substring(spaceIndex).trim();
			        			
			        			lineCount++;
			        			
			        			if(lineMap.containsKey(lineCount+".0")){
			        				bw.write(lineMap.get(lineCount+".0") + "\t" + thisLine + "\n");
			        				int count = 1;
			        				while(lineMap.containsKey(lineCount+"."+count)){
			        					bw.write(lineMap.get(lineCount+"."+count) + "\t" + thisLine + "\n");
			        					count++;
			        				}
			        			}
			        			else
			        				bw.write("MISSING CODE\t" + thisLine + "\n");
			        		}
			        		
			        		if(line.length() > 0){
			        			lineCount++;
			        			if(lineMap.containsKey(lineCount+".0")){
			        				bw.write(lineMap.get(lineCount+".0") + "\t" + line + "\n");
			        				int count = 1;
			        				while(lineMap.containsKey(lineCount+"."+count)){
			        					bw.write(lineMap.get(lineCount+"."+count) + "\t" + line + "\n");
			        					count++;
			        				}
			        			}
			        			else
			        				bw.write("MISSING CODE\t" + line + "\n");
			        		}
			        	}
			        	else{
			        		lineCount++;
			        		if(lineMap.containsKey(lineCount+".0")){
		        				bw.write(lineMap.get(lineCount+".0") + "\t" + line + "\n");
		        				int count = 1;
		        				while(lineMap.containsKey(lineCount+"."+count)){
		        					bw.write(lineMap.get(lineCount+"."+count) + "\t" + line + "\n");
		        					count++;
		        				}
		        			}
		        			else
		        				bw.write("MISSING CODE\t" + line + "\n");
			        	}
			            line = br.readLine().trim();	            
			        }
			        
			        lineCount++;
			        while(lineMap.containsKey(lineCount+".0")){	
        				bw.write(lineMap.get(lineCount+".0") + "\t" + line + "\n");
        				int count = 1;
        				while(lineMap.containsKey(lineCount+"."+count)){
        					bw.write(lineMap.get(lineCount+"."+count) + "\t" + line + "\n");
        					count++;
        				}
        				lineCount++;
			        }
			        
			        br.close();
			        bw.flush();
			        bw.close();
			        
			    } catch(Exception e) {
			        br.close();
			        bw.close();
			    }
			}
		}
	}
	
	public static Map<String, String> getLineMapping(String fileName){
		Map<String, String> lineMap = new HashMap<>();		
		BufferedReader br = null;
		 
	    try {
	    	br  = new BufferedReader(new FileReader(fileName));	
	        String[] oneLine;
	        String line = br.readLine();

	        while (line != null) {
	        	oneLine = line.split(",");
	        	if(oneLine.length > 5)
	        		lineMap.put(oneLine[3].trim(), (oneLine[5]+"").trim());
	        	else if(oneLine.length > 3)
	        		lineMap.put(oneLine[3].trim(), "MISSING CODE");
	            
	            line = br.readLine();	            
	        }
	        
	        br.close();
	        
	    } catch(Exception e) {
	    	if(br != null)
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	    }
	    
		return lineMap;
	}
}

class TimestampAndIndex{
	String timestamp;
	int idx;
	String who;
}
