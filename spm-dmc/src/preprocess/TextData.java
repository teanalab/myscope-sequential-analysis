package preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class TextData {
	
	public static void createSequences(String inputFolder, String successFile, 
			String unsuccessFile, boolean withSpeaker) throws Exception {
		
		PrintWriter successWriter = new PrintWriter(successFile);
		PrintWriter unsuccessWriter = new PrintWriter(unsuccessFile);
		File rawDataFolder = new File(inputFolder);
		File []rawFiles = rawDataFolder.listFiles();
		 
		for (int i = 0; i < rawFiles.length; ++i) {
			BufferedReader br = new BufferedReader(new FileReader(rawFiles[i]));			
			String line = "";
			int count = 0;
			
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				String[] arr = line.split(",");
				String code = arr[0].trim();
				
				//System.out.println(rawFiles[i].getName() + " : " + arr[1]);
				
				if (withSpeaker) {
					String pr = arr[1].trim().split(":")[0].trim();
					if(pr.equalsIgnoreCase("O") || pr.equalsIgnoreCase("O2")
							|| pr.equalsIgnoreCase("S2") || pr.equalsIgnoreCase("S3")) {
						code = "O:" + code;
					}
					else if(pr.equalsIgnoreCase("Y") || pr.equalsIgnoreCase("PY")
							 || pr.equalsIgnoreCase("PT")) {
						code = "PT:" + code;
					}
					else if(pr.equalsIgnoreCase("CG") || pr.equalsIgnoreCase("CG2")) {
						code = "CG:" + code;
					}
					else {
						code = "CHW:" + code;
					}
				}
				
				if (arr[0].trim().equalsIgnoreCase("CHT+") || arr[0].trim().equalsIgnoreCase("CML+") 
						|| arr[0].trim().equalsIgnoreCase("AMB+")) {
					sb.append("500");
					if (count > 1)
						successWriter.println(sb.toString());
					sb.setLength(0);
					count = 0;
				}
				else if (arr[0].trim().equalsIgnoreCase("CHT-") || arr[0].trim().equalsIgnoreCase("CML-") 
						|| arr[0].trim().equalsIgnoreCase("AMB-") || arr[0].trim().equalsIgnoreCase("AMB0")) {
					sb.append("400");
					if (count > 1)
						unsuccessWriter.println(sb.toString());
					sb.setLength(0);
					count = 0;
				}
				else {
					count++;
					sb.append(code);
					sb.append(",");
				}
			}
			
			br.close();	
		}
		successWriter.close();
		unsuccessWriter.close();
	}
	
	public static void createSequencesFrom37MITranscripts(String inputFolder, String successFile, 
			String unsuccessFile, boolean withSpeaker) throws Exception {
		
		Map<String, String> speakerCodeMap = getMaping("codebook.txt", "\\s+");
		Map<String, String> smallerCodeMap = getMaping("codebook for mapping.txt", ",");
		
		PrintWriter successWriter = new PrintWriter(successFile);
		PrintWriter unsuccessWriter = new PrintWriter(unsuccessFile);
		File rawDataFolder = new File(inputFolder);
		File []rawFiles = rawDataFolder.listFiles();
		 
		for (int i = 0; i < rawFiles.length; ++i) {
			BufferedReader br = new BufferedReader(new FileReader(rawFiles[i]));			
			String line = "";
			int count = 0;
			
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				String[] arr = line.split("\\s+");
				String code = speakerCodeMap.get(arr[0].trim());
				
				//System.out.println(rawFiles[i].getName() + " : " + arr[0].trim());
			
				
				String short_code = smallerCodeMap.get(speakerCodeMap.get(arr[0].trim())).trim();
				if (withSpeaker) {
					code = arr[1].trim().toUpperCase() + short_code;
				}
				
				if (short_code.equalsIgnoreCase("CHTP") || short_code.equalsIgnoreCase("CMLP")) {
					sb.append("500");
					if (count > 1 && !sb.toString().contains("OMIT")){
						successWriter.println(sb.toString());
					}
					sb.setLength(0);
					count = 0;
				}
				else if (short_code.equalsIgnoreCase("ST")) {
					sb.append("400");
					if (count > 1 && !sb.toString().contains("OMIT")){
						unsuccessWriter.println(sb.toString());
					}
					sb.setLength(0);
					count = 0;
				}
				else {
					count++;
					sb.append(code);
					sb.append(",");
				}				
			}
			
			br.close();	
		}
		successWriter.close();
		unsuccessWriter.close();
	}
	
	public static Map<String, String> getMaping(String inputFile, String regex) {
		Map<String, String> speakerCodeMap = new TreeMap<>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFile));			
			String line = null;
			
			while((line = br.readLine()) != null) {				
				String[] codes = line.split(regex);
				speakerCodeMap.put(codes[0].trim(), codes[1].trim());
			}
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return speakerCodeMap;
	}
}
