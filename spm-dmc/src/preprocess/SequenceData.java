package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class SequenceData {
	
	private HashMap<String, Integer> dict = new HashMap<>();
	
	public SequenceData() {}
	
	public SequenceData(String filePath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			
			String line = null;
			int x = 1;
			
			while((line = br.readLine()) != null) {
				
				String[] codes = line.split(",");				
				
				for (String code : codes) {
					if (code.equalsIgnoreCase("400") || code.equalsIgnoreCase("500")) {
						dict.put(code, -2);
					}
					else {
						if (!dict.containsKey(code)) {
							dict.put(code, x++);
						}
					}
				}
			}
			
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void transformToExpectedFormat(String fromFilePath, String toFilePath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fromFilePath));
			BufferedWriter writer = new BufferedWriter(new FileWriter(toFilePath));			
			String line = null;
			
			while((line = br.readLine()) != null) {
				
				String[] codes = line.split(",");
				StringBuilder sb = new StringBuilder();
				
				for (String code : codes) {
					String newCode = "";
					if (code.equalsIgnoreCase("400") || code.equalsIgnoreCase("500")) 
						newCode = "" + dict.get(code);
					else
						newCode = dict.get(code) + " -1 ";
						
					sb.append(newCode);
				}
				
				writer.write(sb.toString() + "\n");
			}
			
			br.close();
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
