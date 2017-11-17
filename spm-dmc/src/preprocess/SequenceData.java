package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SequenceData {
	
	public Map<String, Integer> dict = new HashMap<>();
	public Map<Integer, String> intToCodeMap = new HashMap<>();
	
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
						intToCodeMap.put(-2, code);
					}
					else {
						if (!dict.containsKey(code)) {
							intToCodeMap.put(x, code);
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
	
	public void transformToSequentialPatternsFormat(String fromFilePath, String toFilePath) {
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
	
	public void transformToFrequentItemsetFormat(String fromFilePath, String toFilePath, boolean isUnique) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fromFilePath));
			BufferedWriter writer = new BufferedWriter(new FileWriter(toFilePath));			
			String line = null;
			
			while((line = br.readLine()) != null) {
				
				String[] codes = line.split(",");
				StringBuilder sb = new StringBuilder();
				Map<String, String> map = new HashMap<>();
				
				for (String code : codes) {
					String newCode = "";
					if (!(code.equalsIgnoreCase("400") || code.equalsIgnoreCase("500"))){ 
						newCode = dict.get(code) + " ";
						if (!map.containsKey(dict.get(code).toString())) {
							sb.append(newCode);
							map.put(dict.get(code).toString(), dict.get(code).toString());
						}
						else {
							if(!isUnique)
								sb.append(newCode);
						}
					}
						
				}
				
				writer.write(sb.toString().trim() + "\n");
			}
			
			br.close();
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void transformToClientFormat(String fromFilePath, String toFilePath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fromFilePath));
			BufferedWriter writer = new BufferedWriter(new FileWriter(toFilePath));			
			String line = null;
			
			while((line = br.readLine()) != null) {
				
				String[] codes = line.split(",");
				StringBuilder sb = new StringBuilder();
				
				for (String code : codes) {
					String newCode = "";
					if (code.equalsIgnoreCase("400") || code.equalsIgnoreCase("500")){ 
						sb.append(code);
						break;
					}
					else {
						if(code.split(":")[0].equalsIgnoreCase("CG") || code.split(":")[0].equalsIgnoreCase("T"))
							newCode = "CL:" + code.split(":")[1].trim();
						else
							newCode = code;
					}
						
					sb.append(newCode);
					sb.append(",");
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
