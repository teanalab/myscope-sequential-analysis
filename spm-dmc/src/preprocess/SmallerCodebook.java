package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SmallerCodebook {
	public Map<String, String> smallCodebookMap;
	
	public SmallerCodebook() {
		smallCodebookMap = new HashMap<>();
	}
	
	public SmallerCodebook(String filePath) {
		smallCodebookMap = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));			
			String line = null;
			
			while((line = br.readLine()) != null) {				
				String[] codes = line.split(",");								
				smallCodebookMap.put(codes[0].trim(), codes[1].trim());
			}
			
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeAllDistinctCode(String filePath) {
		try {
			
			Map<String, String> tmap = new TreeMap<>(); 
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));			
			
			for(String val : smallCodebookMap.values()) {
				tmap.put(val, val);
			}
			
			for(String val : tmap.values()) {
				if (!(val.equalsIgnoreCase("400") || val.equalsIgnoreCase("500") || val.equalsIgnoreCase("OMIT"))) {
					writer.write(val);
					writer.newLine();
				}
			}
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void transform(String fromFilePath, String toFilePath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fromFilePath));			
			BufferedWriter writer = new BufferedWriter(new FileWriter(toFilePath));			
			String line = null;
			
			while((line = br.readLine()) != null) {				
				String[] codes = line.split(",");
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < codes.length; i++) {
					if(!smallCodebookMap.get(codes[i].trim()).equalsIgnoreCase("OMIT")) {
						sb.append(smallCodebookMap.get(codes[i].trim()));
						if(i < codes.length-1) {
							sb.append(",");
						}
					}
				}
				writer.write(sb.toString());
				writer.newLine();
			}
			
			br.close();
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
